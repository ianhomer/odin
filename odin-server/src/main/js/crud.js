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

const VERIFY_IGNORE_PROPERTIES = ['_links.self.href'];

function setFieldValue(entity, schemaId, schema, refs, name, key) {
  var value = getFieldValue(schemaId, schema, refs, name, key);
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
function getFieldValue(schemaId, schema, refs, name, key, required = true) {
  var _key = key ? key : name;
  var value;
  if (schema && schema.properties[name] && schema.properties[name]['$ref']) {

    // Navigate through object definition to find property names.
    var refSchemaId = getRefSchemaId(schemaId, schema.properties[name]['$ref']);
    var fieldSchema = getSchema(refSchemaId);
    var property = {};
    Object.keys(fieldSchema.properties).map(function(propertyName) {
      var propertyKey = _key + '.' + propertyName;
      property[propertyName] = getFieldValue(refSchemaId, fieldSchema, refs, propertyName, propertyKey);
    });
    value = property;
  } else if (schema && schema.properties[name] && schema.properties[name].type == 'object') {
    // TODO : Handle better than just JSON to object
    var node = ReactDOM.findDOMNode(refs[_key]);
    if (node === null) {
      value = null;
    } else {
      value = JSON.parse(node.value.trim());
    }
  } else {
    var node = ReactDOM.findDOMNode(refs[_key]);
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

function getRefSchemaId(id, ref = '') {
  return id + ref;
}

function getSchema(id) {
  var internalSchema = ajv.getSchema(id);
  if (internalSchema) {
    return ajv.getSchema(id).schema;
  } else {
    console.error('Cannot get schema for ' + id);
    return [];
  }
}

function isSchemaLoaded(id) {
  return ajv.getSchema(id) != null;
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
    return getSchema(getRefSchemaId(this.props.path, schema.properties[name]['$ref']));
  },

  // Exported method for get schema

  getSchema : function(id = this.props.path, ref = '') {
    return getSchema(getRefSchemaId(id, ref));
  },

  isSchemaLoaded : function(id = this.props.path, ref = '') {
    return isSchemaLoaded(getRefSchemaId(id, ref));
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
    var schemaId = this.props.path;
    var schema = getSchema(schemaId);
    Object.keys(schema.properties).map(function(name) {
      setFieldValue(entity, schemaId, schema, this.refs, name);
    }, this);
    // TODO : https://facebook.github.io/react/docs/refs-and-the-dom.html => string refs are now legacy
    setFieldValue(entity, schemaId, schema, this.refs, '_links.self.href');
    var path = getFieldValue(schemaId, schema, this.refs, 'path', null, false);
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

  // Patch entity via REST API.
  onPatch : function(href, patch, callback) {
    client({
      method: 'PATCH',
      path: href,
      entity: patch,
      headers: {
        'Content-Type': 'application/json-patch+json'
        // TODO : Etag support, note that entity needs to be loaded from server prior to
        // editing to populate Etag
        //'If-Match': entity.headers.Etag
      }
    }).done(_response => {
      if (callback) {
        callback();
      }
      // this.loadFromServer();
    }, response => {
      if (response.status && response.status.code === 400) {
        console.error('DENIED: Unable to patch ' + href + '. Perhaps client state is stale.');
      } else {
        console.error('Unable to patch ' + href + ' : ' + response);
      }
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
        console.error('DENIED: Unable to update ' +
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