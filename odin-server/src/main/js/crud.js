/*
 * Functions to create, read, update and delete entities
 */
const ReactDOM = require('react-dom');

const client = require('./client');
const follow = require('./follow');

const root = '/api';

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
    that.handleCreateSubmit = this.handleCreateSubmit.bind(that);
    that.loadFromServer = this.loadFromServer.bind(that);
    that.onCreate = this.onCreate.bind(that);
    that.onDelete = this.onDelete.bind(that);
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
      console.log(JSON.stringify(entities))
      this.setState({
        entities: entities,
        attributes: Object.keys(this.schema.properties),
        pageSize: pageSize,
        links: collection.entity._links});
    });
  },

	handleCreateSubmit(e) {
		e.preventDefault();
		var newEntity = {};
		this.state.attributes.forEach(attribute => {
			newEntity[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.onCreate(newEntity);

		// clear out the dialog's inputs
		this.state.attributes.forEach(attribute => {
			ReactDOM.findDOMNode(this.refs[attribute]).value = '';
		});
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