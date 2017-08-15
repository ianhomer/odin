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

const React = require('react');
const CardLayers = require('./cardLayers');

// Pattern card.
class PatternCard extends React.Component{
  constructor(props) {
    super(props);

    this.state = {
      entity: this.props.entity
    };
  }

  render() {
    return (
      // View card
      <div className="sequence card">
        <header>{this.state.entity.name}</header>
        <div className="content">{this.state.entity.bits}</div>
        <footer><CardLayers entity={this.state.entity}/></footer>
      </div>
    );
  }
}

module.exports = PatternCard;