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

const Trash = require('./trash');
const Layer = require('./layer');

class LayerList extends React.Component{
  constructor(props) {
    super(props);

    this.state = {
      // TODO : Can we remove links?
      schema: [], entities: [], links: []
    };

    crud.bindMe(this);
    this.handleDelete = this.handleDelete.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleKeyPress = this._handleKeyPress.bind(this);
    this.handleNewLayer = this.handleNewLayer.bind(this);
    this.onCreate = this.onCreate.bind(this);
    this.onDelete = this.onDelete.bind(this);
  }

  handleDelete(entity) {
    this.onDelete(entity);
  }

  handleChange() {
    this.props.onChange();
  }

  // Create new layer
  handleNewLayer(e) {
    e.preventDefault();
    var value = e.target.value.trim();
    this.onCreate({
      name: value,
      project: this.props.project._links.self.href
    }, 'layers');
  }

  _handleKeyPress(e) {
    if (e.key === 'Enter') {
      this.handleNewLayer(e);
    }
  }

  componentDidMount() {
    this.loadFromServer();
  }


  findRootLayers(layers) {
    // Create array of layer names
    var layerNames = Object.keys(layers);
    var childNames = [];
    this.state.entities.forEach(entity => {
      // ... and remove any that are children
      entity.layers.forEach(layerName => childNames.push(layerName));
    });
    var rootLayerNames = layerNames.filter(name => !childNames.includes(name));
    // Then collect the remaining layers as the root layers
    var rootLayers = [];
    rootLayerNames.forEach(layerName => rootLayers.push(layers[layerName]));
    return rootLayers;
  }

  render() {
    var layers = {};
    this.state.entities.forEach(layer => layers[layer.name] = layer);
    var sequences = {};
    this.props.sequences.forEach(sequence => sequences[sequence.name] = sequence);

    var rootLayers = this.findRootLayers(layers).map(entity => {
      return (
        <Layer entity={entity} layers={layers} sequences={sequences}
          key={entity._links.self.href} onDelete={this.onDelete}
          onChange={this.handleChange} onDelete={this.handleDelete}/>
      );
    });
    return (
      <div>
        <div>{rootLayers}</div>
        <Trash/>
        <div className="break">&nbsp;</div>
        <div>
          create new layer :&nbsp;
          <input key="new-layer-name" type="text" className="inline"
            onKeyPress={this.handleKeyPress}
            size="8" maxLength="8"
          />
        </div>
      </div>
    );
  }
}

LayerList.defaultProps = {
  path: 'layers'
};

module.exports = LayerList;