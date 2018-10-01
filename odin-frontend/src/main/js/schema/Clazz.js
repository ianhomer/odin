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

// Clazz schema class responsible for defining the structure of a given entity type.   An entity
// class has two parts, the front end schema which defines how a given type is structure in the
// front end rendering and the back end schema which defines the structure for the entity sent
// to the back end.  From the context of the sequencer the front end structure is a specific plugin
// which will have custom fields depending on how the plugin is defined.  From a user perspective
// we want to make the UI aware of the specific plugin to make life easier for the user.  From a
// a back end REST service point of view specific plugins are transmitted in a generic structure
// that corresponds to the given REST end point and internally to the database structure.  This
// class provides the intelligence to allow the front end build intuitive UI and for the entities
// to be appropriately serialised when sent to the back-end.

import { inspect } from 'util'

const objectPath = require('object-path')

const IMPLICIT_PROPERTIES = ['_links.self.href']

const _backEndClazz = new WeakMap()
const _frontEndSchema = new WeakMap()

export class Clazz {
  constructor(getClazz, id, frontEndSchema, backEndClazz) {
    this.id = id
    _frontEndSchema.set(this, frontEndSchema)
    // private variable
    var properties = {}
    // Copy front end schema variables into properties
    for (var propertyName in frontEndSchema.properties) {
      properties[propertyName] = frontEndSchema.properties[propertyName]
    }

    if (backEndClazz) {
      _backEndClazz.set(this, backEndClazz)
      // Copy back end schema variables into properties that have not been defined by front end.
      // However do NOT set the property named properties since this is only for the purposes
      // of the back end class.
      for (var backEndPropertyName in backEndClazz._getProperties()) {
        if (!(backEndPropertyName in properties) && backEndPropertyName !== 'properties') {
          properties[backEndPropertyName] = backEndClazz.getProperty(backEndPropertyName)
        }
      }
    } else {
      _backEndClazz.set(this, this)
    }

    this._getProperties = function() { return properties }
    this.path = backEndClazz ? backEndClazz.id : id
    this.getClazz = getClazz
  }

  getProperty(name) {
    return this._getProperties()[name]
  }

  getPropertyNames() {
    return Object.keys(this._getProperties())
  }

  isPropertyOfType(name, type) {
    return this.getProperty(name) && this.getProperty(name).type === type
  }

  arePropertiesEmpty() {
    return !this._getProperties()
  }

  hasProperty(name) {
    return name in this._getProperties()
  }

  // Create new instance of this class from the references to the properties from the UI
  newInstance(refs) {
    var entity = {}
    // Loop through the properties defined for the class and set the fields in the entity
    // for each of these properties.
    this.getPropertyNames().forEach(name => {
      var definition = this.getProperty(name)
      if (!definition.readOnly) {
        this.setFieldValue(entity, refs, name)
      }
    }, this)
    return entity
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
      var definition = this.getProperty(name)
      if (definition == null) {
        if (!IMPLICIT_PROPERTIES.includes(name)) {
          console.error('Why is definition null?  Trying to set property ' + name + ' in ' + entity)
        }
      } else {
        if (definition.type === 'array') {
          value = value.split(',')
        }
      }
      if (this.getBackEndClazz().hasProperty(name) || IMPLICIT_PROPERTIES.includes(name)) {
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
    if (this.getProperty(name) && this.getProperty(name)['$ref']) {
      // Navigate through object definition to find property names.
      var fieldClazz = this.getClazz(this.id, this.getProperty(name)['$ref'])
      var property = {}
      fieldClazz.getPropertyNames().forEach(propertyName => {
        var propertyKey = _key + '.' + propertyName
        property[propertyName] = fieldClazz.getFieldValue(nodes, propertyName, propertyKey)
      })
      value = property
    } else {
      node = nodes[_key]
      if (node) {
        var trimmed = node.value.trim()
        if (this.isPropertyOfType(name, 'integer')) {
          value = parseInt(trimmed, 10)
        } else if (this.isPropertyOfType(name, 'object')) {
          // TODO : Handle better than just JSON to object
          value = JSON.parse(trimmed)
        } else {
          value = trimmed;
        }
      } else {
        if (required) {
          console.error('Cannot find field ' + _key + ' in node ' + inspect(nodes, {'depth' : 1}))
          value = ''
        } else {
          value = null
        }
      }
    }
    return value
  }

  toString() {
    JSON.stringify(this.getProperties())
  }
}