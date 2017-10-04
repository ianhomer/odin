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
import {connect} from 'react-redux'
import Composer from '../components/composer/Composer'
import {createEntityRequested, deleteEntityRequested,
  addLayerToEntityRequested, moveLayerRequested, removeLayerRequested,
  fetchCompositionRequested, loadEntitiesRequested} from '../actions'

class ComposerContainer extends React.Component {
  constructor(props) {
    super(props)
  }

  componentDidMount() {
    this.props.onLoadEntities('sequence', this.props.schema)
    this.props.onLoadEntities('layer', this.props.schema)
  }

  render() {
    return (
      <div>
        {this.props.sequences && this.props.layers &&
          <Composer
            schema={this.props.schema} project={this.props.project}
            sequences={this.props.sequences} layers={this.props.layers}
            onCreate={this.props.onCreate}
            onDelete={this.props.onDelete}
            onAddLayer={this.props.onAddLayer}
            onMoveLayer={this.props.onMoveLayer}
            onRemoveLayer={this.props.onRemoveLayer}
            onFetchComposition={this.props.onFetchComposition}
          />
        }
      </div>
    )
  }
}

function mapStateToProps() {
  return function(state) {
    const layers = state.rest.layer ? state.rest.layer : {entities: []}
    const sequences = state.rest.sequence ? state.rest.sequence : {entities: []}

    return {
      layers,
      sequences
    }
  }
}

export function mapDispatchToProps(dispatch) {
  return {
    onAddLayer: (entity, layer) => {
      dispatch(addLayerToEntityRequested(entity, layer))
    },
    onCreate: (entity, path) => {
      dispatch(createEntityRequested(entity, path))
    },
    onDelete: entity => {
      dispatch(deleteEntityRequested(entity))
    },
    onMoveLayer: (destination, from, layer) => {
      dispatch(moveLayerRequested(destination, from, layer))
    },
    onRemoveLayer: (entity, layer) => {
      dispatch(removeLayerRequested(entity, layer))
    },
    onFetchComposition: (entityName, notation) => {
      dispatch(fetchCompositionRequested(entityName, notation))
    },
    onLoadEntities: (path, schema) => {
      dispatch(loadEntitiesRequested(path, schema))
    }
  }
}

ComposerContainer.propTypes = {
  layers: PropTypes.object.isRequired,
  onAddLayer: PropTypes.func.isRequired,
  onCreate: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onMoveLayer: PropTypes.func.isRequired,
  onRemoveLayer: PropTypes.func.isRequired,
  onFetchComposition: PropTypes.func.isRequired,
  onLoadEntities: PropTypes.func.isRequired,
  project: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired,
  sequences: PropTypes.object.isRequired
}

export default connect(mapStateToProps, mapDispatchToProps)(ComposerContainer)