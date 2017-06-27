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

// Rendering of generic sequence - NOT CURRENTLY USED.
class Sequence extends React.Component{
  render() {
    return (
      <tr>
        <td>{this.props.sequence.channel}</td>
        <td>{this.props.sequence.bits}</td>
        <td>
          <div className="tick">
            {(this.props.sequence.tick.denominator > 1 || this.props.sequence.tick.numerator > 1) &&
              <span className="numerator">{this.props.sequence.tick.numerator}</span>
            }
            {this.props.sequence.tick.denominator > 1 &&
              <span className="denominator">/{this.props.sequence.tick.denominator}</span>
            }
            &nbsp;{this.props.sequence.tick.timeUnit}
          </div>
        </td>
        <td>
          {this.props.sequence.flowName}
          {JSON.stringify(this.props.sequence)}
        </td>
      </tr>
    );
  }
}

module.exports = Sequence;