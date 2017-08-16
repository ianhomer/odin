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
const SequenceInLayer = require('./sequenceInLayer');

class Layer extends React.Component{
  render() {
    var sequenceNames = this.props.sequenceNames.map(entry =>
      <SequenceInLayer key={'layer-' + this.props.entity.name + '-' + entry.name}
        name={entry.name} layerIndex={entry.index} href={entry.href}/>
    );
    return (
      <div className="layer card">
        {this.props.entity.name}
        <div className="sequences">{sequenceNames}</div>
      </div>
    );
  }
}

module.exports = Layer;