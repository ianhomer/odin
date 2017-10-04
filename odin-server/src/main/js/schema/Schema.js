// Copyright (c) 2017 The Odin Authors. All Rights Reserved
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// System schema class

const Ajv = require('ajv')

import {Clazz} from './Clazz'

const root = '/api'
const _ajv = new WeakMap()
const _clazzes = new WeakMap()
const _flux = new WeakMap()
const _schema = new WeakMap()

const draft04 = require('ajv/lib/refs/json-schema-draft-04.json')

export class Schema {
  constructor(schema, flux = {}) {
    // ajv is a private property
    _ajv.set(this, new Ajv({extendRefs: true}))
    this.getAjv().addMetaSchema(draft04)
    // schema is a private property
    _schema.set(this, schema)
    _flux.set(this, flux)
    _clazzes.set(this, {})
    this.revision = schema.revision
    // Initialise from project schema, i.e. project schema from Odin schema service
    if (schema.project) {
      for (var urn in schema.project.types) {
        this.getAjv().addSchema(schema.project.types[urn], urn)
      }
    }
    // Initialise from profile schemas, i.e. REST endpoint profiles
    if (schema.profiles) {
      for (var key in schema.profiles) {
        this.addSchemaForClazz(schema.profiles[key], key)
      }
    }
  }

  getAjv() {
    return _ajv.get(this)
  }

  getClazzes() {
    return _clazzes.get(this)
  }

  getFlux() {
    return _flux.get(this)
  }

  getSchema() {
    return _schema.get(this)
  }

  getFlowClazz(flowName) {
    var urn = this.getSchema().project.flows[flowName]
    return this.getClazz(urn)
  }

  // Get the schema definition for a given field name.

  getFieldClazz(path, name) {
    var clazz = this.getClazz(path)
    var definition = clazz.properties[name]
    if (definition == null) {
      throw 'Cannot get clazz definition for ' + name
    }
    return this.getClazz(this.getRefClazzId(path, definition['$ref']))
  }

  // Exported method for get schema

  getClazz(id, ref = '') {
    var fullId = this.getRefClazzId(id, ref)
    var clazz = this.getClazzes()[fullId]
    if (clazz == null) {
      clazz = this.createClazzFromId(fullId)
      if (clazz == null) {
        throw 'Cannot get clazz from ID ' + fullId
      }
      this.getClazzes()[fullId] = clazz
    }
    return clazz
  }

  createClazzFromId(id) {
    var internalSchema = this.getAjv().getSchema(id)
    if (internalSchema) {
      return new Clazz(this.getClazz.bind(this), id,
        this.getClazzSchema(id), this.getBackEndClazz(id))
    } else {
      var message = 'Cannot create clazz for ' + id + ', schema has not been loaded'
      console.error(message)
      throw message
    }
  }

  // Get the schema for the clazz stored on the back end
  getBackEndClazz(id) {
    if (Object.values(this.getSchema().project.flows).includes(id) && id != 'sequence') {
      return this.createClazzFromId('sequence')
    }
  }

  getClazzSchema(id) {
    var internalSchema = this.getAjv().getSchema(id)
    if (!internalSchema) {
      throw 'Schema for ' + id + ' has not been loaded'
    }
    return internalSchema.schema
  }

  // TODO : Change to isSchemaLoaded
  isClazzLoaded(id, ref = '') {
    return this.getAjv().getSchema(this.getRefClazzId(id, ref)) != null
  }

  areSchemasLoaded(ids) {
    for (var i = 0 ; i < ids.length ; i++) {
      if (!this.isClazzLoaded(ids[i])) {
        return false
      }
    }
    return true
  }

  // load multiple schemas

  loadClazzes(paths) {
    var promises = []
    paths.forEach(path => {
      promises.concat(this.loadClazz(path))
    })
    return Promise.all(promises)
  }

  // Load schema if it has not already been loaded

  loadClazz(path) {
    return new Promise((resolve, reject) => {
      var schema = this.getAjv().getSchema(path)
      if (!schema) {
        console.warn('loadClazz(' + path + ') should not be called anymore')
        this.getFlux().client({
          method: 'GET',
          path: root + '/profile/' + path,
          headers: {'Accept': 'application/schema+json'}
        }).then(response => {
          this.addSchemaForClazz(response.entity, path)
          resolve(response.entity)
        }).catch(reason => {
          reject(reason)
        })
      } else {
        resolve(schema)
      }
    })
  }

  addSchemaForClazz(schema, path) {
    if (!this.getAjv().getSchema(path)) {
      this.getAjv().addSchema(schema, path)
    }
  }

  // Get schema ID for the given ID and ref combination.
  //
  // e.g. getRefClazzId('patterns','#/definitions/tick')

  getRefClazzId(id, ref = '') {
    return id + ref
  }

  // Handle creation or update an entity.
  handleApply(e, refs, clazzId, onApply) {
    e.preventDefault()
    var entity = {}
    var clazz = this.getClazz(clazzId)
    Object.keys(clazz.properties).map(function(name) {
      var definition = clazz.properties[name]
      if (!definition.readOnly) {
        clazz.setFieldValue(entity, refs, name)
      }
    }, this)
    var path = clazz.getFieldValue(refs, 'path', null, false)
    if (path) {
      onApply(entity, path)
    } else {
      // TODO : https://facebook.github.io/react/docs/refs-and-the-dom.html => string refs are now legacy
      clazz.setFieldValue(entity, refs, '_links.self.href')
      onApply(entity)
    }
  }
}