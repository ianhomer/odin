/*
 * Functions to handle pagination
 */

module.exports = {
  updatePageSize : function (pageSize) {
   if (pageSize !== this.state.pageSize) {
     this.loadFromServer('channels', pageSize);
   }
  },

  onNavigate : function(navUri) {
    client({method: 'GET', path: navUri}).done(entities => {
      this.setState({
        entities: entities.entity._embedded.channels,
        attributes: this.state.attributes,
        pageSize: this.state.pageSize,
        links: entities.entity._links
      });
    });
  }
}