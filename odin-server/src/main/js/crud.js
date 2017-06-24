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
const client = require('./client');
const follow = require('./follow');

const root = '/api';

const ajv = new Ajv({extendRefs : true});
ajv.addMetaSchema(require('ajv/lib/refs/json-schema-draft-04.json'));

// Get value of field from from fields, e.g. after form submit.
function getFieldValue(schema, refs, name, key) {
  var key = key ? key : name;
  var value;
  if (schema.properties[name]['$ref']) {
    /*
     * Navigate through object definition to find property names.
     */
    var fieldSchema = getSchema('patterns', schema.properties[name]['$ref']);
    var property = {};
    Object.keys(fieldSchema.properties).map(function(propertyName) {
      var propertyKey = key + '.' + propertyName;
      property[propertyName] = getFieldValue(fieldSchema, refs, propertyName, propertyKey);
      console.log("test " + propertyName + ":" + property[propertyName] + ":" + JSON.stringify(property));
    });
    value = property;
  } else {
    var node = ReactDOM.findDOMNode(refs[key]);
    value = node === null ? "" : node.value.trim();
  }
  console.log("Got field value : " + key + " = " + JSON.stringify(value));
  return value;
}

// Get schema for the given ID and ref combination.
//
// e.g. getSchema('patterns','#/definitions/tick')
function getSchema(id, ref = '') {
  console.log("Getting schema " + id + ref);
  return ajv.getSchema(id + ref).schema;
}

// Load entities from the server.
//
// Which entities loaded is determined by this.props.path.  For example this might be set
// 'channels' to load the channels.  Page size is controlled by this.state.pageSize.
//
// The side effect of this function is that the attributes 'entities', 'attributes' and
// 'links' are set in this.state.
//
module.exports = {
  bindMe : function(that) {
    that.loadFromServer = this.loadFromServer.bind(that);
    that.onDelete = this.onDelete.bind(that);
    that.onCreate = this.onCreate.bind(that);
    that.onUpdate = this.onUpdate.bind(that);
  },

  // Get the schema definition for a given field name.
  getSchemaDefinition : function(name) {
    return getSchema(this.props.path, this.props.schema.properties[name]['$ref']);
  },

  loadFromServer : function(pageSize = this.state.pageSize) {
    console.log("Loading " + this.props.path)
    follow(client, root, [
      {rel: this.props.path, params: {size: pageSize}}]
    ).then(collection => {
      return client({
        method: 'GET',
        path: collection.entity._links.profile.href,
        headers: {'Accept': 'application/schema+json'}
      }).then(schema => {
        this.schema = schema.entity;
        return collection;
      });
    }).done(collection => {
      var entities = [];

      // Load all the entities by concatenating all embedded entities

      for (var key in collection.entity._embedded) {
        entities = entities.concat(collection.entity._embedded[key]);
      }
      console.log("Loaded entities : " + JSON.stringify(entities));

      // Register the schema.

      if (!ajv.getSchema(this.props.path)) {
        console.log("Registering schema : " + this.props.path)
        ajv.addSchema(this.schema, this.props.path);
      }

      // Set the state.

      var schema = getSchema(this.props.path);
      console.log("Schema : " + JSON.stringify(schema));
      this.setState({
        entities: entities,
        schema: getSchema(this.props.path),
        pageSize: pageSize,
        links: collection.entity._links});
    });
  },


  // Handle creation or update an entity.
	handleApply(e) {
	  console.log("Handling entity apply");
		e.preventDefault();
		var entity = {};
		Object.keys(this.props.schema.properties).map(function(name) {
		  var value = getFieldValue(this.props.schema, this.refs, name);
		  if (value) {
			  entity[name] = value;
			}
		}, this);
		console.log("Applying entity : " + JSON.stringify(entity));
		this.props.onApply(entity);
	},

  // Create entity via REST API.
  onCreate : function(entity) {
    follow(client, root, [this.props.path]).then(entities => {
      return client({
        method: 'POST',
        path: entities.entity._links.self.href,
        entity: entity,
        headers: {'Content-Type': 'application/json'}
      })
    }).then(response => {
      return follow(client, root, [
        {rel: this.props.path, params: {'size': this.state.pageSize}}]);
    }).done(response => {
      this.loadFromServer();
    });
  },

  // Update entity via REST API.
  onUpdate : function(entity) {
    client({
      method: 'PUT',
      path: entity.entity._links.self.href,
      entity: entity,
      headers: {
        'Content-Type': 'application/json',
        'If-Match': employee.headers.Etag
      }
    }).done(response => {
      if (this.props.onApplySuccess) {
        this.onApplySuccess();
      }
      this.loadFromServer(this.props.entityListComponent.state.pageSize);
    }, response => {
      if (response.status.code === 412) {
        alert('DENIED: Unable to update ' +
          entity.entity._links.self.href + '. Your copy is stale.');
      }
    });
  },

  onDelete : function(entity) {
    client({method: 'DELETE', path: entity._links.self.href}).done(response => {
      this.loadFromServer();
    });
  }
}