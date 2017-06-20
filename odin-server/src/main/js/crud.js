/*
 * Functions to create, read, update and delete entities
 */
const ReactDOM = require('react-dom');
const Ajv = require('ajv');
const client = require('./client');
const follow = require('./follow');

const root = '/api';

function getFieldValue(schema, refs, name) {
  if (schema.properties[name].type == 'object') {
    console.log("Read object TODO");
  } else {
    var node = ReactDOM.findDOMNode(refs[name]);
    return node === null ? "" : node.value.trim();
  }
};

/*
 * Load entities from the server.
 *
 * Which entities loaded is determined by this.props.path.  For example this might be set
 * 'channels' to load the channels.  Page size is controlled by this.state.pageSize.
 *
 * The side effect of this function is that the attributes 'entities', 'attributes' and
 * 'links' are set in this.state.
 */
module.exports = {
  bindMe : function(that) {
    that.loadFromServer = this.loadFromServer.bind(that);
    that.onDelete = this.onDelete.bind(that);
    that.onCreate = this.onCreate.bind(that);
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
      for (var key in collection.entity._embedded) {
        entities = entities.concat(collection.entity._embedded[key]);
      }
      var ajv = new Ajv({extendRefs : true});
      ajv.addMetaSchema(require('ajv/lib/refs/json-schema-draft-04.json'));
      if (this.props.path == 'patterns') {
        ajv.addSchema(this.schema, 'patterns');
        console.log("Tick Schema : " + JSON.stringify(ajv.getSchema('patterns#/definitions/tick').schema));
      }
      this.setState({
        entities: entities,
        schema: this.schema,
        pageSize: pageSize,
        links: collection.entity._links});
    });
  },

  /*
   * Handle creation of an entity when form submitted.
   */
	handleCreateSubmit(e) {
		e.preventDefault();
		var newEntity = {};
		Object.keys(this.props.schema.properties).map(function(name) {
		  console.log("Looking for node " + name + ":" + this);
			newEntity[name] = getFieldValue(this.props.schema, this.refs, name);
		}, this);
		console.log("Creating new entity : " + JSON.stringify(newEntity));
		this.props.onCreate(newEntity);
	},

  onCreate : function(newEntity) {
    follow(client, root, [this.props.path]).then(entities => {
      return client({
        method: 'POST',
        path: entities.entity._links.self.href,
        entity: newEntity,
        headers: {'Content-Type': 'application/json'}
      })
    }).then(response => {
      return follow(client, root, [
        {rel: this.props.path, params: {'size': this.state.pageSize}}]);
    }).done(response => {
      if (typeof response.entity._links.last != "undefined") {
        this.onNavigate(response.entity._links.last.href);
      } else {
        this.onNavigate(response.entity._links.self.href);
      }
      this.loadFromServer();
    });
  },

  onDelete : function(entity) {
    client({method: 'DELETE', path: entity._links.self.href}).done(response => {
      this.loadFromServer();
    });
  }
}