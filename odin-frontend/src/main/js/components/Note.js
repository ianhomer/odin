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

class Note extends React.Component{
  constructor(props) {
    super(props)
  }

  render() {
    return (
      <div className="row">
        <div className="col-4">{this.props.number}</div>
        <div className="col-4">{this.props.velocity}</div>
        <div className="col-4">{this.props.numerator}</div>
      </div>
    )
  }
}

// TODO : Should numerator be required?
// TODO : Should number, velocity be a number?
Note.propTypes = {
  number: PropTypes.string.isRequired,
  numerator: PropTypes.number,
  velocity: PropTypes.string.isRequired
}

export default Note



