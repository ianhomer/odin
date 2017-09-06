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

// System schema class

const ReactDOM = require('react-dom');

const objectPath = require('object-path');
const Ajv = require('ajv');
const ajv = new Ajv({extendRefs : true});
ajv.addMetaSchema(require('ajv/lib/refs/json-schema-draft-04.json'));

const client = require('../client');

const VERIFY_IGNORE_PROPERTIES = ['_links.self.href'];
const root = '/api';

export class Schema {
  constructor(schema) {
    this.schema = schema
  }

  getFlowSchema(flowName) {
    var urn = schema.flows[flowName];
    return schema.types[urn];
  }

  // Get the schema definition for a given field name.

  getSchemaDefinition(path, name) {
    var schema = this.getSchema(path);
    var definition = schema.properties[name];
    if (definition == null) {
      throw 'Cannot get schema definition for ' + name;
    }
    return this.getSchema(this.getRefSchemaId(path, definition['$ref']));
  }

  // Exported method for get schema

  getSchema(id, ref = '') {
    return this.getSchemaFromId(this.getRefSchemaId(id, ref));
  }

  isSchemaLoaded(id, ref = '') {
    return ajv.getSchema(this.getRefSchemaId(id, ref)) != null;
  }

  // load multiple schemas

  loadSchemas(paths) {
    var promises = [];
    paths.forEach(path => {
      promises.concat(this.loadSchema(path));
    });
    return Promise.all(promises);
  }

  // Load schema if it has not already been loaded

  loadSchema(path) {
    return new Promise((resolve, reject) => {
      var schema = ajv.getSchema(path);
      if (!schema) {
        client({
          method: 'GET',
          path: root + '/profile/' + path,
          headers: {'Accept': 'application/schema+json'}
        }).then(response => {
          if (!ajv.getSchema(path)) {
            ajv.addSchema(response.entity, path);
          }
          resolve(response.entity);
        }).catch(reason => {
          reject(reason);
        });
      } else {
        resolve(schema);
      }
    });
  }

  setFieldValue(entity, schemaId, schema, refs, name, key) {
    var value = this.getFieldValue(schemaId, schema, refs, name, key);
    if (value) {
      /*
       * Split array properties.
       */
      var definition = schema.properties[name];
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
  getFieldValue(schemaId, schema, refs, name, key, required = true) {
    var _key = key ? key : name;
    var value;
    var node;
    if (schema && schema.properties[name] && schema.properties[name]['$ref']) {

      // Navigate through object definition to find property names.
      var refSchemaId = this.getRefSchemaId(schemaId, schema.properties[name]['$ref']);
      var fieldSchema = this.getSchema(refSchemaId);
      var property = {};
      for (var propertyName in fieldSchema.properties) {
        var propertyKey = _key + '.' + propertyName;
        property[propertyName] = this.getFieldValue(refSchemaId, fieldSchema, refs, propertyName, propertyKey);
      }
      value = property;
    } else if (schema && schema.properties[name] && schema.properties[name].type == 'object') {
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

  // Get schema ID for the given ID and ref combination.
  //
  // e.g. getRefSchemaId('patterns','#/definitions/tick')

  getRefSchemaId(id, ref = '') {
    return id + ref;
  }

  getSchemaFromId(id) {
    var internalSchema = ajv.getSchema(id);
    if (internalSchema) {
      return ajv.getSchema(id).schema;
    } else {
      console.error('Cannot get schema for ' + id);
      return [];
    }
  }

  // Handle creation or update an entity.
  handleApply(e, refs, path, onApply) {
    e.preventDefault();
    var entity = {};
    var schemaId = path;
    var schema = this.getSchema(schemaId);
    Object.keys(schema.properties).map(function(name) {
      var definition = schema.properties[name];
      if (!definition.readOnly) {
        this.setFieldValue(entity, schemaId, schema, refs, name);
      }
    }, this);
    var path = this.getFieldValue(schemaId, schema, refs, 'path', null, false);
    if (path) {
      onApply(entity, path);
    } else {
      // TODO : https://facebook.github.io/react/docs/refs-and-the-dom.html => string refs are now legacy
      this.setFieldValue(entity, schemaId, schema, refs, '_links.self.href');
      onApply(entity);
    }
  }
}