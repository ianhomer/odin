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

import React from 'react'
import PropTypes from 'prop-types'
import {BrowserRouter as Router, Route} from 'react-router-dom'
import {connect, Provider} from 'react-redux'
import {Schema} from '../schema/schema'
import Project from './project'
import Developer from '../components/developer/developer'

class App extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      schema: this.createNewSchema(props)
    }

    this.renderProject = this.renderProject.bind(this)
    this.renderDeveloper = this.renderDeveloper.bind(this)
  }

  createNewSchema(props) {
    // Only create schema when we have the project schema
    return props.schema.project ? new Schema(props.schema, props.flux) : null
  }

  componentWillReceiveProps(newProps) {
    if (newProps.schema != this.props.schema || newProps.schema.revision != this.props.schema.revision) {
      this.setState({schema: this.createNewSchema(newProps)})
    }
  }

  renderProject() {
    return (
      <div>
        {this.state.schema &&
          <Project schema={this.state.schema}/>
        }
      </div>
    )
  }

  renderDeveloper() {
    return (
      <div>
        {this.state.schema &&
          <Developer/>
        }
      </div>
    )
  }

  render() {
    if (this.state.schema) {
      return (
        <Provider store={this.props.store}>
          <Router>
            <div>
              <Route exact path="/app/developer" component={this.renderDeveloper}/>
              <Route path="/app" component={this.renderProject}/>
            </div>
          </Router>
        </Provider>
      )
    } else {
      return <div>Loading schema ...</div>
    }
  }
}

function mapStateToProps(state) {
  const schema = state.schema

  return {
    schema
  }
}

App.propTypes = {
  schema: PropTypes.object.isRequired,
  store: PropTypes.object.isRequired
}

export default connect(mapStateToProps)(App)
