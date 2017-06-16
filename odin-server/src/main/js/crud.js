/*
 * Functions to create, read, update and delete entities
 */

const client = require('./client');
const follow = require('./follow');

const root = '/api';

/*
 * Load given named entities from the server.
 */
module.exports = {
  loadFromServer : function(name, pageSize) {
    follow(client, root, [
      {rel: name, params: {size: pageSize}}]
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
      this.setState({
        entities: collection.entity._embedded[name],
        attributes: Object.keys(this.schema.properties),
        pageSize: pageSize,
        links: collection.entity._links});
    });
  },

  // tag::create[]
  onCreate : function(newChannel) {
    follow(client, root, ['channels']).then(channelCollection => {
      return client({
        method: 'POST',
        path: channelCollection.entity._links.self.href,
        entity: newChannel,
        headers: {'Content-Type': 'application/json'}
      })
    }).then(response => {
      return follow(client, root, [
        {rel: 'channels', params: {'size': this.state.pageSize}}]);
    }).done(response => {
      if (typeof response.entity._links.last != "undefined") {
        this.onNavigate(response.entity._links.last.href);
      } else {
        this.onNavigate(response.entity._links.self.href);
      }
      this.loadFromServer('channels', this.state.pageSize);
    });
  },
  // end::create[]

  // tag::delete[]
  onDelete : function(entity) {
    client({method: 'DELETE', path: entity._links.self.href}).done(response => {
      this.loadFromServer('channels', this.state.pageSize);
    });
  },
  // end::delete[]

  // tag::navigate[]
  onNavigate : function(navUri) {
    client({method: 'GET', path: navUri}).done(employeeCollection => {
      this.setState({
        employees: employeeCollection.entity._embedded.employees,
        attributes: this.state.attributes,
        pageSize: this.state.pageSize,
        links: employeeCollection.entity._links
      });
    });
  }
  // end::navigate[]
}