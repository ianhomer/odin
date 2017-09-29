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
import {connect} from 'react-redux'
import Composer from '../components/composer/composer'
import {fetchCompositionRequested} from '../actions'

class ComposerContainer extends React.Component {
  constructor(props) {
    super(props)
  }

  render() {
    return (
      <div>
        {this.props.sequences && this.props.layers &&
          <Composer
            schema={this.props.schema} project={this.props.project} flux={this.props.flux}
            sequences={this.props.sequences} layers={this.props.layers}
            onFetchComposition={this.props.onFetchComposition}
            />
        }
      </div>
    )
  }
}

function mapStateToProps(path) {
  return function(state) {
    const layers = state.collections['layers'] ? state.collections['layers'] : {entities: []}
    const sequences = state.collections['sequences'] ? state.collections['sequences'] : {entities: []}

    return {
      layers,
      sequences
    }
  }
}

export function mapDispatchToProps(dispatch) {
  return {
    onFetchComposition: (entityName, notation) => {
      dispatch(fetchCompositionRequested(entityName, notation))
    }
  }
}

ComposerContainer.propTypes = {
  layers: PropTypes.object.isRequired,
  onFetchComposition: PropTypes.func.isRequired,
  project: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired,
  sequences: PropTypes.object.isRequired
}

export default connect(mapStateToProps, mapDispatchToProps)(ComposerContainer)