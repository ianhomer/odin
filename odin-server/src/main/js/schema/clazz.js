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

// Clazz schema class

const objectPath = require('object-path')

const IMPLICIT_PROPERTIES = ['_links.self.href']

const _backEndClazz = new WeakMap()
const _frontEndSchema = new WeakMap()

export class Clazz {
  constructor(getClazz, id, frontEndSchema, backEndClazz) {
    this.id = id
    _frontEndSchema.set(this, frontEndSchema)
    if (backEndClazz) {
      _backEndClazz.set(this, backEndClazz)
    } else {
      _backEndClazz.set(this, this)
    }
    this.path = backEndClazz ? backEndClazz.id : id
    this.properties = frontEndSchema.properties
    this.getClazz = getClazz
  }

  getBackEndClazz() {
    return _backEndClazz.get(this)
  }

  getFrontEndSchema() {
    return _frontEndSchema.get(this)
  }

  setFieldValue(entity, nodes, name, key) {
    var value = this.getFieldValue(nodes, name, key)
    if (value) {
      // Split array properties.
      var definition = this.properties[name]
      if (definition == null) {
        if (!IMPLICIT_PROPERTIES.includes(name)) {
          console.error('Why is definition null?  Trying to set property ' + name + ' in ' + entity)
        }
      } else {
        if (definition.type == 'array') {
          value = value.split(',')
        }
      }
      if (name in this.getBackEndClazz().properties || IMPLICIT_PROPERTIES.includes(name)) {
        // Set the property in the entity if property is defined in back end schema.
        objectPath.set(entity, name, value)
      } else {
        // Otherwise set property in generic properties map and let the back end handle it
        // through the generic properties mechanism.
        if (!entity.properties) {
          entity.properties = {}
        }
        this.setProperty(entity.properties, name, value)
      }
    }
  }

  // Flatten value into multiple property values, e.g. if value is a note which has a few
  // properties it self like number, velocity and duration then this will be written as
  // 3 properties, note.number, note.velocity and note.duration
  setProperty(properties, name, value) {
    if (value == null) {
      console.warn('Why is property ' + name + ' null?')
    }
    if (typeof value === 'object') {
      for (var childName in value) {
        this.setProperty(properties, name + '.' + childName, value[childName])
      }
    } else {
      properties[name] = value
    }
  }

  getEntityValue(entity, name) {
    return objectPath.get(entity, name) || entity.properties[name]
  }

  // Get value of field from from fields, e.g. after form submit.
  getFieldValue(nodes, name, key, required = true) {
    var _key = key ? key : name
    var value
    var node
    if (this.properties[name] && this.properties[name]['$ref']) {
      // Navigate through object definition to find property names.
      var fieldClazz = this.getClazz(this.id, this.properties[name]['$ref'])
      var property = {}
      for (var propertyName in fieldClazz.properties) {
        var propertyKey = _key + '.' + propertyName
        property[propertyName] = fieldClazz.getFieldValue(nodes, propertyName, propertyKey)
      }
      value = property
    // TODO : Reduce duplicated blocks of code below
    } else if (this.properties[name] && this.properties[name].type == 'object') {
      // TODO : Handle better than just JSON to object
      node = nodes[_key]
      if (node) {
        value = node.value.trim()
        if (value) {
          value = JSON.parse(value)
        }
      } else {
        value = null
      }
    } else if (this.properties[name] && this.properties[name].type == 'integer') {
      node = nodes[_key]
      if (node) {
        value = parseInt(node.value.trim())
      } else {
        if (required) {
          console.error('Cannot find field ' + _key + ' in DOM')
          value = ''
        } else {
          value = null
        }
      }
    } else {
      node = nodes[_key]
      if (node) {
        value = node.value.trim()
      } else {
        if (required) {
          console.error('Cannot find field ' + _key + ' in DOM')
          value = ''
        } else {
          value = null
        }
      }
    }
    return value
  }
}