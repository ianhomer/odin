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

import EntitiesContainer from './entitiesContainer'
import SequenceList from '../components/sequenceList'
import {connectEntities, mapDispatchToProps} from './connectEntities'
import {fetchCompositionRequested} from '../actions'

class SequencesContainer extends EntitiesContainer {
  constructor(props) {
    super(props)
    this.component = SequenceList
  }

  getExtraArguments() {
    return {
      onFetchComposition: this.props.onFetchComposition
    }
  }
}

function sequenceMapDispatchToProps(dispatch) {
  return Object.assign({}, mapDispatchToProps(dispatch), {
    onFetchComposition: (entityName, notation) => {
      dispatch(fetchCompositionRequested(entityName, notation))
    }
  })
}

SequencesContainer.defaultProps = {
  path: 'sequence'
}

export default connectEntities(SequencesContainer.defaultProps.path, SequencesContainer,
  sequenceMapDispatchToProps)