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

'use strict'

const React = require('react')
const PropTypes = require('prop-types')

const Pattern = require('./pattern')
const EditEntity = require('./editEntity')

// Pattern list component.
class PatternList extends React.Component{
  constructor(props) {
    super(props)
    this.state = {
      schema: [], entities: [], links: []
    }

    this.loadSchema = this.props.flux.loadSchema.bind(this)
    this.loadFromServer = this.props.flux.loadFromServer.bind(this)
  }

  componentDidMount() {
    this.loadSchema()
    this.loadFromServer()
  }

  // Render patterns.  Note that as per bootstrap requirement element sizes in a row MUST not
  render() {
    var entities = this.state.entities.map(entity =>
      <Pattern entity={entity} key={entity._links.self.href}
        project={this.props.project} flux={this.props.flux}
        onDelete={this.onDelete} onUpdate={this.onUpdate}
      />
    )
    return (
      <div>
        <div className="container">
          <div className="row">
            <div className="col-1">Channel</div>
            <div className="col-1">Offset</div>
            <div className="col-1">Length</div>
            <div className="col-2">Tick</div>
            <div className="col-1">Bits</div>
            <div className="col-2">Note</div>
            <div className="col-3">Flow Name</div>
          </div>
          {this.isSchemaLoaded() &&
            <EditEntity
              project={this.props.project} flux={this.props.flux}
              path={Pattern.defaultProps.path} fields={Pattern.defaultProps.fields}
              onApply={this.onCreate}
            />
          }
          {entities}
        </div>
      </div>
    )
  }
}

PatternList.defaultProps = {
  path: 'pattern'
}

PatternList.propTypes = {
  flux: PropTypes.object.isRequired,
  project: PropTypes.object.isRequired
}

module.exports = PatternList