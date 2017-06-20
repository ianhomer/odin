/*
 * Functions to handle pagination
 */
const client = require('./client');

module.exports = {
  updatePageSize : function (pageSize) {
   if (pageSize !== this.state.pageSize) {
     this.loadFromServer(pageSize);
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
  },

  handlePageSizeInput(e) {
    e.preventDefault();
    var pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
    if (/^[0-9]+$/.test(pageSize)) {
      this.updatePageSize(pageSize);
    } else {
      ReactDOM.findDOMNode(this.refs.pageSize).value =
        pageSize.substring(0, pageSize.length - 1);
    }
  },

  handleNavFirst(e){
    e.preventDefault();
    this.onNavigate(this.state.links.first.href);
  },

  handleNavPrev(e) {
    e.preventDefault();
    this.onNavigate(this.state.links.prev.href);
  },

  handleNavNext(e) {
    e.preventDefault();
    this.onNavigate(this.state.links.next.href);
  },

  handleNavLast(e) {
    e.preventDefault();
    this.onNavigate(this.state.links.last.href);
  }
}