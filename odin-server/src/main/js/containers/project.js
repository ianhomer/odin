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

import {BrowserRouter as Router, Route} from 'react-router-dom'
import {connect} from 'react-redux'

import ChannelsContainer from '../containers/channelsContainer'
import SequencesContainer from '../containers/sequencesContainer'
import ComposerContainer from '../containers/composerContainer'

class Project extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      project: null
    }

    this.renderSequenceList = this.renderSequenceList.bind(this)
    this.renderChannelList = this.renderChannelList.bind(this)
    this.renderComposer = this.renderComposer.bind(this)
  }

  componentDidMount() {
    // Load first project (only one project supported for now)
    this.props.flux.client({method: 'GET', path: '/api/project'}).done(response => {
      var projects = response.entity._embedded.project
      this.setState({project: projects[0]})
    })
  }

  renderChannelList() {
    // TODO : Remove dispatch property if redundant
    return (
      <div>
        {this.state.project &&
          <ChannelsContainer schema={this.props.schema} project={this.state.project} flux={this.props.flux}
            dispatch={this.props.dispatch} />
        }
      </div>
    )
  }

  renderComposer() {
    return (
      <div>
        {this.state.project &&
          <ComposerContainer schema={this.props.schema} project={this.state.project}/>
        }
      </div>
    )
  }

  renderSequenceList() {
    return (
      <div>
        {this.state.project &&
          <SequencesContainer schema={this.props.schema} project={this.state.project} flux={this.props.flux}/>
        }
      </div>
    )
  }

  render() {
    return (
      <Router>
        <div>
          <Route exact path="/app" component={this.renderComposer}/>
          <Route exact path="/app/sequences" component={this.renderSequenceList}/>
          <Route exact path="/app/channels" component={this.renderChannelList}/>
        </div>
      </Router>
    )
  }
}

Project.propTypes = {
  dispatch: PropTypes.func.isRequired,
  flux: PropTypes.shape({
    client: PropTypes.func.isRequired
  }),
  schema: PropTypes.object.isRequired
}

function mapStateToProps(state) {
  const {channels} = state

  return {
    channels
  }
}

export default connect(mapStateToProps)(Project)