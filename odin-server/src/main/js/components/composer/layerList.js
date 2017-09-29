// Copyright (c) 2017 the original author or authors. All Rights Reserved
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



const React = require('react')
const PropTypes = require('prop-types')

const Trash = require('./trash')
const Layer = require('./layer')

class LayerList extends React.Component{
  constructor(props) {
    super(props)

    this.state = {
      // TODO : Can we remove schema and links?
      schema: [], entities: [], links: []
    }

    this.handleDelete = this.handleDelete.bind(this)
    this.handleChange = this.handleChange.bind(this)
    this.handleKeyPress = this._handleKeyPress.bind(this)
    this.handleNewLayer = this.handleNewLayer.bind(this)
    this.onCreate = this.props.flux.onCreate.bind(this)
    this.onDelete = this.props.flux.onDelete.bind(this)
    this.loadFromServer = this.props.flux.loadFromServer.bind(this)
  }

  handleDelete(entity) {
    this.onDelete(entity)
  }

  handleChange() {
    this.loadFromServer()
    this.props.onChange()
  }

  // Create new layer
  handleNewLayer(e) {
    e.preventDefault()
    var value = e.target.value.trim()
    this.onCreate({
      name: value,
      project: this.props.project._links.self.href
    }, 'layer')
  }

  _handleKeyPress(e) {
    if (e.key === 'Enter') {
      this.handleNewLayer(e)
    }
  }

  componentDidMount() {
    this.loadFromServer()
  }


  findRootLayers(layers) {
    // Create array of layer names
    var layerNames = Object.keys(layers)
    var childNames = []
    this.state.entities.forEach(entity => {
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
        parent={parentLayer} flux={this.props.flux}
        key={layer.name}
        onChange={this.handleChange} onDelete={this.handleDelete}
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
    this.state.entities.forEach(layer => layers[layer.name] = layer)
    var sequences = {}
    this.props.sequences.forEach(sequence => sequences[sequence.name] = sequence)

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
  flux: PropTypes.object.isRequired,
  onAddLayer: PropTypes.func.isRequired,
  onChange: PropTypes.func.isRequired,
  onMoveLayer: PropTypes.func.isRequired,
  project: PropTypes.shape({
    _links: PropTypes.shape({
      self: PropTypes.shape({
        href: PropTypes.string.isRequired
      })
    })
  }),
  sequences: PropTypes.array.isRequired,
  schema: PropTypes.object.isRequired
}

module.exports = LayerList