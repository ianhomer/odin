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

// Tick component.
class Tick extends React.Component{
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div className="row">
        <div className="col-md-6">{(this.props.denominator > 1 || this.props.numerator > 1) &&
              <span className="numerator">{this.props.numerator}</span>
        }</div>
        <div className="col-md-6">{this.props.denominator > 1 &&
              <span className="denominator">/{this.props.denominator}</span>
        }&nbsp;</div>
      </div>
    );
  }
}

module.exports = Tick;