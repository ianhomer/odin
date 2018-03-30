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

class EntitiesContainer extends React.Component {
  constructor(props) {
    super(props)
  }

  componentDidMount() {
    this.props.onLoadEntities(this.props.path, this.props.schema)
  }

  getExtraArguments() {
    return {}
  }

  render() {
    return (
      <div>
        {this.props.collection &&
          <this.component schema={this.props.schema} performance={this.props.performance}
            onCreate={this.props.onCreate} onDelete={this.props.onDelete}
            onUpdate={this.props.onUpdate}
            path={this.path}
            collection={this.props.collection}
            {...this.getExtraArguments()}/>
        }
      </div>
    )
  }
}

EntitiesContainer.propTypes = {
  collection: PropTypes.object.isRequired,
  onCreate: PropTypes.func.isRequired,
  onUpdate: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onLoadEntities: PropTypes.func.isRequired,
  path: PropTypes.string.isRequired,
  performance: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired
}

export default EntitiesContainer