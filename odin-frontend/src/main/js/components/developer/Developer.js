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

// Rendering of developer console
class Developer extends React.Component{
  constructor(props) {
    super(props)
    this.state = {
      loggers: {}
    }

    this.handleKeyPress = this._handleKeyPress.bind(this)
    this.handleLoggingLevelChange = this.handleLoggingLevelChange.bind(this)
    this.loadFromServer = this.loadFromServer.bind(this)
  }

  // Handle logging level change
  handleLoggingLevelChange(e) {
    e.preventDefault()
    var value = e.target.value.trim()
    var parts = value.split(':')
    var level, category
    if (parts.length == 2) {
      level = parts[0].trim()
      category = parts[1].trim()
    } else {
      level = 'DEBUG'
      category = value
    }

    // Find a matching category if one exists
    var existingCategories = Object.keys(this.state.loggers)
    if (!existingCategories.includes(category)) {
      for (var existingCategory in this.state.loggers) {
        if (existingCategory.endsWith(category) || existingCategory.startsWith(category)) {
          category = existingCategory
          break
        }
      }
    }

    fetch('/actuator/loggers/' + category, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }, body: JSON.stringify({configuredLevel: level})
    })
      .then(this.loadFromServer).catch(reason => console.error(reason))
      .catch(reason => console.error(reason))
  }

  _handleKeyPress(e) {
    if (e.key === 'Enter') {
      this.handleLoggingLevelChange(e)
    }
  }

  componentDidMount() {
    this.loadFromServer()
  }

  loadFromServer() {
    fetch('/actuator/loggers', {
      method: 'GET',
      headers: {'Accept': 'application/json'}
    })
      .then(response => response.json())
      .then(json => this.setState({loggers: json.loggers}))
      .catch(reason => console.error(reason))
  }

  renderLoggersAtLevel(matchLevel) {
    return (
      <section key={'logging-' + matchLevel}>
        <input key={matchLevel + '-logging-level'} type="text" className="inline"
          defaultValue={matchLevel + ' : '} size="60"
          onKeyPress={this.handleKeyPress}
        />
        {Object.keys(this.state.loggers).map(name => {
          var level = this.state.loggers[name].configuredLevel
          if (level == matchLevel) {
            return (
              <div className="row" key={'logging-' + name}>
                <div className="col-2">&nbsp;</div>
                <div className="col-6">{name}</div>
              </div>
            )
          }
        })}
      </section>
    )
  }

  render() {
    return (
      // Display developer console
      <div className='logging'>
        <p>Set logging level to view more logging.</p>
        {this.renderLoggersAtLevel('TRACE')}
        {this.renderLoggersAtLevel('DEBUG')}
        {this.renderLoggersAtLevel('INFO')}
        {this.renderLoggersAtLevel('WARN')}
        {this.renderLoggersAtLevel('ERROR')}
      </div>
    )
  }
}

export default Developer