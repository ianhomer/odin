// With thanks to https://spring.io/guides/tutorials/react-and-spring-data-rest/
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

'use strict';

// Functions to create, read, update and delete entities

const ReactDOM = require('react-dom');
const Ajv = require('ajv');
const objectPath = require('object-path');

const client = require('./client');
const follow = require('./follow');

const root = '/api';

const ajv = new Ajv({extendRefs : true});
ajv.addMetaSchema(require('ajv/lib/refs/json-schema-draft-04.json'));

function setFieldValue(entity, schema, refs, name, key) {
  var value = getFieldValue(schema, refs, name, key);
  if (value) {
    objectPath.set(entity, name, value);
  }
}

// Get value of field from from fields, e.g. after form submit.
function getFieldValue(schema, refs, name, key) {
  var _key = key ? key : name;
  var value;
  if (schema && schema.properties[name] && schema.properties[name]['$ref']) {

    // Navigate through object definition to find property names.

    // TODO : Why does this read 'patterns', shouldn't we get the appropriate schema dynamically
    var fieldSchema = getSchema('patterns', schema.properties[name]['$ref']);
    var property = {};
    Object.keys(fieldSchema.properties).map(function(propertyName) {
      var propertyKey = _key + '.' + propertyName;
      property[propertyName] = getFieldValue(fieldSchema, refs, propertyName, propertyKey);
    });
    value = property;
  } else {
    var node = ReactDOM.findDOMNode(refs[_key]);
    if (node === null) {
      console.error('Cannot find field ' + _key);
      value = '';
    } else {
      value = node.value.trim();
    }
  }
  return value;
}

// Get schema for the given ID and ref combination.
//
// e.g. getSchema('patterns','#/definitions/tick')

function getSchema(id, ref = '') {
  var internalSchema = ajv.getSchema(id + ref);
  if (internalSchema) {
    return ajv.getSchema(id + ref).schema;
  } else {
    console.error('Cannot get schema for ' + id + ref);
    return [];
  }
}

function isSchemaLoaded(id, ref = '') {
  return ajv.getSchema(id + ref) != null;
}


// Load entities from the server.
//
// Which entities loaded is determined by this.props.path.  For example this might be set
// 'channels' to load the channels.
//
// The side effect of this function is that the attributes 'entities', 'attributes' and
// 'links' are set in this.state.

module.exports = {
  bindMe : function(that) {
    that.loadFromServer = this.loadFromServer.bind(that);
    that.loadSchema = this.loadSchema.bind(that);
    that.isSchemaLoaded = this.isSchemaLoaded.bind(that);
    that.onDelete = this.onDelete.bind(that);
    that.onCreate = this.onCreate.bind(that);
    that.onUpdate = this.onUpdate.bind(that);
  },

  // Get the schema definition for a given field name.

  getSchemaDefinition : function(name) {
    var schema = getSchema(this.props.path);
    return getSchema(this.props.path, schema.properties[name]['$ref']);
  },

  // Exported method for get schema

  getSchema : function(id = this.props.path, ref = '') {
    return getSchema(id, ref);
  },

  isSchemaLoaded : function(id = this.props.path, ref = '') {
    return isSchemaLoaded(id, ref);
  },

  // load multiple schemas

  loadSchemas : function(paths) {
    var promises = [];
    paths.forEach(path => {
      promises.concat(this.loadSchema(path));
    });
    return Promise.all(promises);
  },

  // Load schema if it has not already been loaded

  loadSchema : function(path = this.props.path) {
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
  },

  loadFromServer : function() {
    this.loadSchema().then(() => {
      follow(client, root, [{rel: this.props.path}]).done(collection => {
        var entities = [];

        // Load all the entities by concatenating all embedded entities.

        for (var path in collection.entity._embedded) {
          var embeddedEntities = collection.entity._embedded[path];

          // Set the path value in the entity so that the front end knows what type of entity
          // this is.

          if (Array.isArray(embeddedEntities)) {
            for (var key in embeddedEntities) {
              embeddedEntities[key].path = path;
            }
          } else {
            embeddedEntities.path = path;
          }
          entities = entities.concat(embeddedEntities);
        }

        // Set the state.

        this.setState({
          entities: entities
        });
      });
    });
  },

  // Handle creation or update an entity.
  handleApply(e) {
    e.preventDefault();
    var entity = {};
    var schema = getSchema(this.props.path);
    Object.keys(schema.properties).map(function(name) {
      setFieldValue(entity, schema, this.refs, name);
    }, this);
    // TODO : https://facebook.github.io/react/docs/refs-and-the-dom.html => string refs are now legacy
    setFieldValue(entity, null, this.refs, '_links.self.href');
    var path = getFieldValue(schema, this.refs, 'path');
    if (path) {
      this.props.onApply(entity, path);
    } else {
      this.props.onApply(entity);
    }
  },

  // Create entity via REST API.
  onCreate : function(entity, path) {
    follow(client, root, [path]).then(entities => {
      return client({
        method: 'POST',
        path: entities.entity._links.self.href,
        entity: entity,
        headers: {'Content-Type': 'application/json'}
      });
    }).done(_response => {
      this.loadFromServer();
    });
  },

  // Update entity via REST API.
  onUpdate : function(entity) {
    client({
      method: 'PUT',
      path: entity._links.self.href,
      entity: entity,
      headers: {
        'Content-Type': 'application/json'
        // TODO : Etag support, note that entity needs to be loaded from server prior to
        // editing to populate Etag
        //'If-Match': entity.headers.Etag
      }
    }).done(_response => {
      this.setState({
        entity: entity,
        editing: null
      });
      // this.loadFromServer();
    }, response => {
      if (response.status.code === 412) {
        alert('DENIED: Unable to update ' +
          entity.entity._links.self.href + '. Your copy is stale.');
      }
    });
  },

  onDelete : function(entity) {
    client({method: 'DELETE', path: entity._links.self.href}).done(_response => {
      this.loadFromServer();
    });
  }
};