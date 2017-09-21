// Copyright (c) 2017 the original author or authors. All Rights Reserved
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
const ajv = new Ajv({extendRefs : true})
ajv.addMetaSchema(require('ajv/lib/refs/json-schema-draft-04.json'))

import { Clazz } from './clazz'

const root = '/api'

export class Schema {
  constructor(schema, flux) {
    this.schema = schema
    this.flux = flux
    this.clazzes = {}
    for (var urn in schema.types) {
      ajv.addSchema(schema.types[urn], urn)
    }
  }

  getFlowClazz(flowName) {
    var urn = this.schema.flows[flowName]
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
    var clazz = this.clazzes[fullId]
    if (clazz == null) {
      clazz = this.createClazzFromId(fullId)
      if (clazz == null) {
        throw 'Cannot get clazz from ID ' + fullId
      }
      this.clazzes[fullId] = clazz
    }
    return clazz
  }

  createClazzFromId(id) {
    var internalSchema = ajv.getSchema(id)
    if (internalSchema) {
      return new Clazz(this.getClazz.bind(this), id,
        this.getClazzSchema(id), this.getBackEndClazz(id))
    } else {
      throw 'Cannot create clazz for ' + id + ' schema has not been loaded'
    }
  }

  // Get the schema for the clazz stored on the back end
  getBackEndClazz(id) {
    if (Object.values(this.schema.flows).includes(id) && id != 'sequence') {
      return this.createClazzFromId('sequence')
    }
  }

  getClazzSchema(id) {
    return ajv.getSchema(id).schema
  }


  // TODO : Change to isSchemaLoaded
  isClazzLoaded(id, ref = '') {
    return ajv.getSchema(this.getRefClazzId(id, ref)) != null
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
    if (!ajv.getSchema(path)) {
      return new Promise((resolve, reject) => {
        var schema = ajv.getSchema(path)
        if (!schema) {
          this.flux.client({
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
  }

  addSchemaForClazz(schema, path) {
    if (!ajv.getSchema(path)) {
      ajv.addSchema(schema, path)
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