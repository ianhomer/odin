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

class Channel extends React.Component{
  constructor(props) {
    super(props)
    this.handleDelete = this.handleDelete.bind(this)
  }

  handleDelete() {
    this.props.onDelete(this.props.entity)
  }

  render() {
    return (
      <div className="row">
        <div className="col-2">{this.props.entity.number}</div>
        <div className="col-3">{this.props.entity.programName}</div>
        <div className="col-1">
          <button type="submit" className="btn btn-primary" onClick={this.handleDelete}>Delete</button>
        </div>
      </div>
    )
  }
}

Channel.propTypes = {
  entity: PropTypes.shape({
    number: PropTypes.number.isRequired,
    programName: PropTypes.string.isRequired
  }),
  onDelete: PropTypes.func.isRequired
}

export default Channel
