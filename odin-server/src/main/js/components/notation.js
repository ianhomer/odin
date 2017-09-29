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

import Sequence from './sequence'
import Score from './score'

// Notation component.
class Notation extends Sequence {
  constructor(props) {
    super(props)
  }

  getExtraEditEntityArguments() {
    return {
      onFetchComposition: this.props.onFetchComposition
    }
  }

  renderProperties(sequence) {
    return (
      <Score onFetchComposition={this.props.onFetchComposition}
        entity={sequence} displayText={true} width={800}/>
    )
  }
}

Notation.propTypes = Object.assign({}, Sequence.propTypes,
  {
    onFetchComposition: PropTypes.func.isRequired
  })

Notation.defaultProps = {
  path: 'notation',
  fields: {
    'name': {size: 8, maxLength: 8},
    'format': {
      cellWidth: 1,
      defaultValue: 'natural',
      hidden: true
    },
    'notation': {
      cellWidth: 3,
      defaultValue: 'C'
    },
    'tick': {
      cellWidth: 2,
      fields: {
        'numerator': {defaultValue: 1, cellWidth: 6},
        'denominator': {defaultValue: 1, cellWidth: 6, label: '/'},
        'timeUnit': {defaultValue: 'BEAT', hidden: true}
      }
    },
    'offset': {defaultValue: 0},
    'length': {defaultValue: -1},
    'channel': {defaultValue: 1},
    'flowName': {
      cellWidth: 2,
      defaultValue: 'notation',
      readOnly: true
    },
    'layers': {
      defaultValue: '',
      hidden: true
    }
  }
}

module.exports = Notation