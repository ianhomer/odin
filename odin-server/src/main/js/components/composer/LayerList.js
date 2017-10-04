// Copyright (c) 2017 The Odin Authors. All Rights Reserved
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

import React from 'react'
import PropTypes from 'prop-types'

import Trash from './Trash'
import Layer from './Layer'

class LayerList extends React.Component{
  constructor(props) {
    super(props)

    this.handleKeyPress = this._handleKeyPress.bind(this)
    this.handleNewLayer = this.handleNewLayer.bind(this)
  }

  // Create new layer
  handleNewLayer(e) {
    e.preventDefault()
    var value = e.target.value.trim()
    this.props.onCreate({
      name: value,
      project: this.props.project._links.self.href
    }, 'layer')
  }

  _handleKeyPress(e) {
    if (e.key === 'Enter') {
      this.handleNewLayer(e)
    }
  }

  findRootLayers(layers) {
    // Create array of layer names
    var layerNames = Object.keys(layers)
    var childNames = []
    this.props.layers.entities.forEach(entity => {
      // ... and remove any that are children
      entity.layers.forEach(layerName => childNames.push(layerName))
    })
    var rootLayerNames = layerNames.filter(name => !childNames.includes(name))
    // Then collect the remaining layers as the root layers
    var rootLayers = []
    rootLayerNames.forEach(layerName => {
      var layer = layers[layerName]
      if (layer == null) {
        console.warn('Cannot find layer ' + layerName + ' as root layer')
      } else {
        rootLayers.push(layer)
      }
    })
    return rootLayers
  }

  renderLayer(layer, parentLayer, layers, sequences, stack) {
    if (stack.includes(layer.name)) {
      return <div key={layer.name}>(recursive : {layer.name})</div>
    }
    stack.push(layer.name)
    var component =
      <Layer entity={layer} layers={layers} sequences={sequences}
        parent={parentLayer}
        key={layer.name}
        onDelete={this.props.onDelete}
        onRemoveLayer={this.props.onRemoveLayer}
        onMoveLayer={this.props.onMoveLayer}
        onAddLayer={this.props.onAddLayer}>
        {layer.layers.map(layerName => {
          var childLayer = layers[layerName]
          if (childLayer == null) {
            return <div key={layerName}>?{layerName}?</div>
          } else {
            return this.renderLayer(childLayer, layer, layers, sequences, stack)
          }
        })}
      </Layer>

    stack.pop()
    return component
  }

  render() {
    var layers = {}
    this.props.layers.entities.forEach(layer => layers[layer.name] = layer)
    var sequences = {}
    this.props.sequences.entities.forEach(sequence => sequences[sequence.name] = sequence)

    var stack = []
    var renderedLayers = this.findRootLayers(layers).map(entity =>
      this.renderLayer(entity, null, layers, sequences, stack)
    )

    return (
      <div>
        <div>{renderedLayers}</div>
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
    )
  }
}

LayerList.defaultProps = {
  path: 'layer'
}

LayerList.propTypes = {
  layers: PropTypes.object.isRequired,
  onAddLayer: PropTypes.func.isRequired,
  onCreate: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onMoveLayer: PropTypes.func.isRequired,
  onRemoveLayer: PropTypes.func.isRequired,
  project: PropTypes.shape({
    _links: PropTypes.shape({
      self: PropTypes.shape({
        href: PropTypes.string.isRequired
      })
    })
  }),
  sequences: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired
}

module.exports = LayerList