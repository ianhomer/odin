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

// System schema class.  Schemas are loaded by redux events e.g. LOAD_PROFILE_SCHEMA_REQUESTED
// is fired and then LOAD_PROFILE_SCHEMA_SUCCEEDED is fired when the web service responds
// which updates to state using the reducers/schema.js which in turn is picked up by App.js
// in the createNewSchema method.   Then when an entity is updated or created the handleApply
// method in this Schema class is called and uses this schema definition to construct the
// appropriate JSON to send to the back end, again via these redux events.   This decoupling
// helps separate concerns (front end state / back end state / front end rendering), allows
// events to be handled asynchronously and makes unit testing of the front end a lot easier.

import {Clazz} from './Clazz'
import {apiRoot} from '../constants'

const Ajv = require('ajv')
const _ajv = new WeakMap()
const _clazzes = new WeakMap()
const _flux = new WeakMap()
const _schema = new WeakMap()

export class Schema {
  constructor(schema, flux = {}) {
    // ajv is a private property
    var ajv = new Ajv({
      meta: false, // optional, to prevent adding draft-06 meta-schema
      extendRefs: true, // optional, current default is to 'fail', spec behaviour is to 'ignore',
      schemaId: 'id',
      unknownFormats: 'ignore'  // optional, current default is true (fail)
    })
    var draft04 = require('ajv/lib/refs/json-schema-draft-04.json')
    ajv.addMetaSchema(draft04)
    ajv._opts.defaultMeta = draft04.id
    _ajv.set(this, ajv)
    // Java odin API still uses draft 4 for the schema

    // schema is a private property
    _schema.set(this, schema)
    _flux.set(this, flux)
    _clazzes.set(this, {})
    this.revision = schema.revision
    // Initialise from performance schema, i.e. performance schema from Odin schema service
    if (schema.performance) {
      for (var urn in schema.performance.types) {
        this.getAjv().addSchema(schema.performance.types[urn], urn)
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

  getFlowClazz(type) {
    var urn = this.getSchema().performance.flows[type]
    return this.getClazz(urn)
  }

  // Get the schema definition for a given field name.

  getFieldClazz(path, name) {
    var clazz = this.getClazz(path)
    var definition = clazz.getProperty(name)
    if (definition == null) {
      throw new Error('Cannot get clazz definition for ' + name)
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
        throw new Error('Cannot get clazz from ID ' + fullId)
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
    if (Object.values(this.getSchema().performance.flows).includes(id) && id !== 'sequence') {
      return this.createClazzFromId('sequence')
    }
  }

  getClazzSchema(id) {
    var internalSchema = this.getAjv().getSchema(id)
    if (!internalSchema) {
      throw new Error('Schema for ' + id + ' has not been loaded')
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
          path: apiRoot + '/rest/profile/' + path,
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
    var clazz = this.getClazz(clazzId)
    var entity = clazz.newInstance(refs)

    var path = clazz.getFieldValue(refs, 'path', null)
    if (path) {
      onApply(entity, path)
    } else {
      // TODO : https://facebook.github.io/react/docs/refs-and-the-dom.html => string refs are now legacy
      clazz.setFieldValue(entity, refs, '_links.self.href')
      onApply(entity)
    }
  }
}