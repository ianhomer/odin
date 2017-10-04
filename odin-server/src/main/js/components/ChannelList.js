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
import Channel from './Channel'
import EditEntity from './EditEntity'

class ChannelList extends React.Component{
  constructor(props) {
    super(props)
    this.state = {
      entities: [], links: [], loaded: false
    }
  }

  render() {
    var entities = this.props.collection.entities.map(entity =>
      <Channel
        entity={entity} key={entity.number}
        onDelete={this.props.onDelete}/>
    )

    return (
      <div>
        <div className="container">
          <div className="row">
            <div className="col-2">Number</div>
            <div className="col-3">Program</div>
          </div>
          {this.props.schema.isClazzLoaded(this.props.path) &&
            <EditEntity
              schema={this.props.schema} project={this.props.project}
              clazz={this.props.schema.getClazz(this.props.path)} fields={this.props.fields}
              onApply={this.props.onCreate}
            />
          }
          {entities}
        </div>
      </div>
    )
  }
}

ChannelList.defaultProps = {
  path: 'channel',
  fields: {
    'number': {
      defaultValue: 1,
      cellWidth: 2
    },
    'programName': {
      defaultValue: 'piano',
      cellWidth: 3
    },
    'program': {
      defaultValue: 0,
      hidden: true
    }
  }
}

ChannelList.propTypes = {
  collection: PropTypes.object.isRequired,
  fields: PropTypes.object.isRequired,
  onCreate: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  path: PropTypes.string.isRequired,
  project: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired
}

module.exports = ChannelList