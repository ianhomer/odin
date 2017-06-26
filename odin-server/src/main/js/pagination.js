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

/*eslint-env node*/

'use strict';

// Functions to handle pagination
const client = require('./client');
const ReactDOM = require('react-dom');

module.exports = {
  bindMe : function(that) {
    that.handleNavFirst = this.handleNavFirst.bind(that);
    that.handleNavPrev = this.handleNavPrev.bind(that);
    that.handleNavNext = this.handleNavNext.bind(that);
    that.handleNavLast = this.handleNavLast.bind(that);
    that.handlePageSizeInput = this.handlePageSizeInput.bind(that);
    that.updatePageSize = this.updatePageSize.bind(that);
    that.onNavigate = this.onNavigate.bind(that);
  },

  updatePageSize : function (pageSize) {
    if (pageSize !== this.state.pageSize) {
      this.loadFromServer(pageSize);
    }
  },

  // TODO : Review whether this function is still needed.  It used to be called from within onCreate
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

  handlePageSizeInput : function(e) {
    e.preventDefault();
    var pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
    if (/^[0-9]+$/.test(pageSize)) {
      this.updatePageSize(pageSize);
    } else {
      ReactDOM.findDOMNode(this.refs.pageSize).value =
        pageSize.substring(0, pageSize.length - 1);
    }
  },

  handleNavFirst : function(e){
    e.preventDefault();
    this.onNavigate(this.state.links.first.href);
  },

  handleNavPrev : function(e) {
    e.preventDefault();
    this.onNavigate(this.state.links.prev.href);
  },

  handleNavNext : function(e) {
    e.preventDefault();
    this.onNavigate(this.state.links.next.href);
  },

  handleNavLast: function(e) {
    e.preventDefault();
    this.onNavigate(this.state.links.last.href);
  }
};