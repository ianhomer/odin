// Copyright (c) 2017 Ian Homer. All Rights Reserved
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

const ReactDOM = require('react-dom');

const objectPath = require('object-path');

const VERIFY_IGNORE_PROPERTIES = ['_links.self.href'];

export class Clazz {
  constructor(id, clazzSchema, getClazz) {
    this.id = id;
    this.clazzSchema = clazzSchema;
    this.properties = clazzSchema.properties;
    this.getClazz = getClazz;
  }

  setFieldValue(entity, refs, name, key) {
    var value = this.getFieldValue(refs, name, key);
    if (value) {
      /*
       * Split array properties.
       */
      var definition = this.properties[name];
      if (definition == null) {
        if (!VERIFY_IGNORE_PROPERTIES.includes(name)) {
          console.error('Why is definition null?  Trying to set property ' + name + ' in ' + entity);
        }
      } else {
        if (definition.type == 'array') {
          value = value.split(',');
        }
      }
      /*
       * Set the property in the entity.
       */
      objectPath.set(entity, name, value);
    }
  }

  // Get value of field from from fields, e.g. after form submit.
  getFieldValue(refs, name, key, required = true) {
    var _key = key ? key : name;
    var value;
    var node;
    if (this.properties[name] && this.properties[name]['$ref']) {

      // Navigate through object definition to find property names.
      var fieldClazz = this.getClazz(this.id, this.properties[name]['$ref']);
      var property = {};
      for (var propertyName in fieldClazz.properties) {
        var propertyKey = _key + '.' + propertyName;
        property[propertyName] = fieldClazz.getFieldValue(refs, propertyName, propertyKey);
      }
      value = property;
    } else if (this.properties[name] && this.properties[name].type == 'object') {
      // TODO : Handle better than just JSON to object
      node = ReactDOM.findDOMNode(refs[_key]);
      if (node === null) {
        value = null;
      } else {
        value = node.value.trim();
        if (value) {
          value = JSON.parse(value);
        }
      }
    } else {
      node = ReactDOM.findDOMNode(refs[_key]);
      if (node === null) {
        if (required) {
          console.error('Cannot find field ' + _key + ' in DOM');
          value = '';
        } else {
          value = null;
        }
      } else {
        value = node.value.trim();
      }
    }
    return value;
  }
}