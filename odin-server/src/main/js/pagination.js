/*
 * Functions to handle pagination
 */

module.exports = {
  // tag::update-page-size[]
  updatePageSize : function (pageSize) {
   if (pageSize !== this.state.pageSize) {
     this.loadFromServer('channels', pageSize);
   }
  }
  // end::update-page-size[]
}