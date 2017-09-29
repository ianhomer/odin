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

import Sequence from './sequence'
const Note = require('./note')

// Pattern component.
class Pattern extends Sequence {
  constructor(props) {
    super(props)
  }

  renderProperties(sequence) {
    return (
      <div className="row">
        <div className="col-4">{sequence.properties.bits}</div>
        <div className="col-8 component">{sequence.properties ?
          <Note
            number={sequence.properties['note.number']}
            velocity={sequence.properties['note.velocity']}
            numerator={sequence.properties['note.numerator']}/>
          : <div className="warn">NULL note</div>
        }</div>
      </div>
    )
  }
}

Pattern.propTypes = Sequence.propTypes

Pattern.defaultProps = {
  path: 'pattern',
  fields: {
    'name': { size: 8, maxLength: 8},
    'bits': { defaultValue: 1 },
    'note': {
      cellWidth: 2,
      fields: {
        'number': {defaultValue: 60, cellWidth: 4, maxLength: 3},
        'velocity': {defaultValue: 100, cellWidth: 4, maxLength: 3},
        'numerator': {defaultValue: 1, cellWidth: 4},
        'denominator': {defaultValue: 1, hidden: true}
      }
    },
    'tick': {
      cellWidth: 2,
      fields: {
        'numerator': {defaultValue: 1, cellWidth: 6},
        'denominator': {defaultValue: 1, cellWidth: 6, label: '/'},
        'timeUnit': {defaultValue: 'BEAT', hidden: true}
      }
    },
    'offset': { defaultValue: 0 },
    'length': { defaultValue: -1 },
    'channel': { defaultValue: 1 },
    'flowName': {
      cellWidth: 2,
      defaultValue: 'pattern',
      readOnly: true
    },
    'layers': {
      defaultValue: '',
      hidden: true
    }
  }
}

module.exports = Pattern