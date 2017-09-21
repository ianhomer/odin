// With thanks to https://spring.io/guides/tutorials/react-and-spring-data-rest/
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

'use strict'

// Functions to create, read, update and delete entities

const client = require('./client')
const follow = require('./follow')

const root = '/api'

// Load entities from the server.
//
// Which entities loaded is determined by this.props.path.  For example this might be set
// 'channels' to load the channels.
//
// The side effect of this function is that the attributes 'entities', 'attributes' and
// 'links' are set in this.state.

module.exports = {
  bindMe : function(that) {
    that.loadFromServer = this.loadFromServer.bind(that)
    that.onDelete = this.onDelete.bind(that)
    that.onCreate = this.onCreate.bind(that)
    that.onUpdate = this.onUpdate.bind(that)
  },

  loadFromServer : function(path = this.props.path, schema = this.props.schema,
    onLoaded = (entities) => { this.setState({entities: entities}) }) {
    schema.loadClazz(path).then(() => {
      follow(client, root, [{rel: path}]).done(collection => {
        var entities = []

        // Load all the entities by concatenating all embedded entities.

        for (var path in collection.entity._embedded) {
          var embeddedEntities = collection.entity._embedded[path]

          // Set the path value in the entity so that the front end knows what type of entity
          // this is.

          if (Array.isArray(embeddedEntities)) {
            for (var key in embeddedEntities) {
              embeddedEntities[key].path = path
            }
          } else {
            embeddedEntities.path = path
          }
          entities = entities.concat(embeddedEntities)
        }

        onLoaded(entities)
      })
    })
  },

  // Create entity via REST API.
  onCreate : function(entity, path, onSuccess = () => { this.loadFromServer() }) {
    follow(client, root, [path]).then(entities => {
      return client({
        method: 'POST',
        path: entities.entity._links.self.href,
        entity: entity,
        headers: {'Content-Type': 'application/json'}
      })
    }).done(_response => {
      onSuccess(_response)
    })
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
        callback()
      }
      // this.loadFromServer();
    }, response => {
      if (response.status && response.status.code === 400) {
        console.error('DENIED: Unable to patch ' + href + '. Perhaps client state is stale.')
      } else {
        console.error('Unable to patch ' + href + ' : ' + response)
      }
    })
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
      })
      // this.loadFromServer();
    }, response => {
      if (response.status.code === 412) {
        console.error('DENIED: Unable to update ' +
          entity.entity._links.self.href + '. Your copy is stale.')
      }
    })
  },

  onDelete : function(entity) {
    client({method: 'DELETE', path: entity._links.self.href}).done(_response => {
      this.loadFromServer()
    })
  }
}