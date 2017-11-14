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



const React = require('react')
const PropTypes = require('prop-types')

import {BrowserRouter as Router, Route} from 'react-router-dom'
import {connect} from 'react-redux'

import ChannelsContainer from '../containers/ChannelsContainer'
import SequencesContainer from '../containers/SequencesContainer'
import ComposerContainer from '../containers/ComposerContainer'

class Performance extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      performance: null
    }

    this.renderSequenceList = this.renderSequenceList.bind(this)
    this.renderChannelList = this.renderChannelList.bind(this)
    this.renderComposer = this.renderComposer.bind(this)
  }

  componentDidMount() {
    // Load first performance (only one performance supported for now)
    // TODO : Move this performance loading over to redux
    fetch('/api/performance', {method: 'GET'})
      .then(response => response.json())
      .then(json => this.setState({performance: json._embedded.performance[0]}))
  }

  renderChannelList() {
    return (
      <div>
        {this.state.performance &&
          <ChannelsContainer schema={this.props.schema} performance={this.state.performance}/>
        }
      </div>
    )
  }

  renderComposer() {
    return (
      <div>
        {this.state.performance &&
          <ComposerContainer schema={this.props.schema} performance={this.state.performance}/>
        }
      </div>
    )
  }

  renderSequenceList() {
    return (
      <div>
        {this.state.performance &&
          <SequencesContainer schema={this.props.schema} performance={this.state.performance}/>
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

Performance.propTypes = {
  dispatch: PropTypes.func.isRequired,
  schema: PropTypes.object.isRequired
}

function mapStateToProps(state) {
  const {channels} = state

  return {
    channels
  }
}

export default connect(mapStateToProps)(Performance)