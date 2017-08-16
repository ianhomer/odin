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

const crud = require('../../crud');

const Layer = require('./layer');

class LayerList extends React.Component{
  constructor(props) {
    super(props);

    this.state = {
      // TODO : Can we remove links?
      schema: [], entities: [], links: []
    };

    crud.bindMe(this);
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange() {
    this.props.onChange();
  }

  componentDidMount() {
    this.loadFromServer();
  }

  render() {
    var entities = this.state.entities.map(entity => {
      var sequences = this.props.sequences;
      var sequencesInLayer = [];
      for (var i = 0 ; i < sequences.length ; i ++) {
        var sequence = sequences[i];
        for (var j = 0 ; j < sequence.layers.length ; j++) {
          var layerName = sequence.layers[j];
          if (layerName == entity.name) {
            // Push a sequence object onto the array with enough information to handle change
            sequencesInLayer.push({
              name : sequence.name,
              index : j,
              href : sequence._links.self.href
            });
          }
        }
      }

      return (
        <Layer entity={entity} sequences={sequencesInLayer}
          key={entity._links.self.href} onDelete={this.onDelete}
          onChange={this.handleChange}/>
      );
    });
    return (
      <div>
        <div>{entities}</div>
      </div>
    );
  }
}

LayerList.defaultProps = {
  path: 'layers'
};

module.exports = LayerList;