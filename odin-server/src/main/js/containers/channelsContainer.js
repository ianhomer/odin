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

'use strict'

const React = require('react')
const PropTypes = require('prop-types')

import { connect } from 'react-redux'

import { createEntityRequested, deleteChannel, loadEntitiesRequested } from '../actions'

import ChannelList from '../components/channelList'

class ChannelsContainer extends React.Component {
  constructor(props) {
    super(props)
  }

  componentDidMount() {
    this.props.onLoadEntities(this.props.schema)
  }

  render() {
    var entities = this.props.entities
    return (
      <div>
        {entities &&
          <ChannelList schema={this.props.schema} project={this.props.project}
            onCreate={this.props.onCreate}
            flux={this.props.flux}
            entities={entities}/>
        }
      </div>
    )
  }
}

function mapStateToProps(state) {
  const entities = state.entities.channel ? state.entities.channel.entities : []

  return {
    entities,
  }
}

const mapDispatchToProps = dispatch => {
  return {
    onCreate : (entity, path) => {
      dispatch(createEntityRequested(entity, path))
    },
    onDeleteChannel: entity => {
      dispatch(deleteChannel(entity))
    },
    onLoadEntities : (schema) => {
      dispatch(loadEntitiesRequested('channel', schema))
    }
  }
}

ChannelsContainer.propTypes = {
  entities: PropTypes.array.isRequired,
  flux: PropTypes.object.isRequired,
  onCreate: PropTypes.func.isRequired,
  schema: PropTypes.object.isRequired
}

export default connect(mapStateToProps, mapDispatchToProps)(ChannelsContainer)