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
import objectPath from 'object-path'
import Score from './Score'

// Edit an entity.
class EditEntity extends React.Component{
  constructor(props) {
    super(props)
    this.state = {properties: {}}
    this.nodes = {}
    this.handleKeyPress = this._handleKeyPress.bind(this)
    this.handleApply = this.handleApply.bind(this)
    this.handleInputChange = this.handleInputChange.bind(this)
  }

  handleInputChange(event) {
    const target = event.target
    const value = target.type === 'checkbox' ? target.checked : target.value
    const name = target.name
    var properties = {}
    objectPath.set(properties, name, value)
    var newState = {properties: {...this.state.properties, ...properties}}
    this.setState(newState)
  }

  handleApply(e) {
    this.props.schema.handleApply(e, this.nodes, this.props.clazz.id, this.props.onApply)
  }

  _handleKeyPress(e) {
    if (e.key === 'Enter') {
      this.handleApply(e)
    }
  }

  registerNode(key) {
    return node => this.nodes[key] = node
  }

  // Render a group of inputs for the specified fields.
  renderInputFieldGroup(clazz, fields, parentKey) {
    if (!fields) {
      console.warn('Fields not defined')
      return <div/>
    }
    if (!clazz) {
      console.warn('Clazz not defined')
      return <div/>
    }
    if (clazz.arePropertiesEmpty()) {
      console.warn('Clazz does not have properties')
      return <div>{clazz.toString()}</div>
    }
    var renderedFields = Object.keys(fields).map(function(fieldName) {
      var key = parentKey ? parentKey + '.' + fieldName : fieldName
      if (fields[fieldName].fields) {
        // Given field is a group of child fields
        var cellWidth = fields[fieldName].cellWidth || 1
        var cellClassName = 'component col-' + cellWidth
        return (
          <div className={cellClassName} key={key}>
            <div className="row">
              {this.renderInputFieldGroup(
                this.props.schema.getFieldClazz(clazz.id, fieldName), fields[fieldName].fields,
                key)}
            </div>
          </div>
        )
      } else {
        return this.renderInputField(clazz, fields, fieldName, key)
      }
    }, this)
    return renderedFields
  }

  renderInputField(clazz, fields, fieldName, key) {
    var field = fields[fieldName]
    var size = 0
    var definition = clazz.getProperty(fieldName)
    var type
    if (definition) {
      type = definition.type
    } else {
      type = 'string'
      console.error('Cannot find attribute : ' + fieldName + ' in ' + clazz.toString())
    }

    if (field.size) {
      size = field.size
    } else {
      if (type == 'integer') {
        size = 3
      }
    }

    var cellWidth = field.cellWidth || 1
    var cellClassName = 'col-' + cellWidth
    var inputClassName = 'property-' + fieldName
    var value = objectPath.get(this.state.properties, key)
    if (value == null) {
      if (this.props.entity) {
        value = clazz.getEntityValue(this.props.entity, key)
        if (type == 'object') {
          // TODO : Handle objects better than string serialisation
          value = JSON.stringify(value)
        }
      } else {
        value = field.defaultValue
      }
    }

    // TODO : Do notation by field type NOT field name
    if (fieldName == 'notation') {
      var scoreEntity = this.props.entity ?
        this.props.entity :
        {properties: {notation: value}}
      if (this.state.properties.notation) {
        // Merge change notation into entity used to render score
        scoreEntity = {...scoreEntity,
          properties: {...scoreEntity.properties, notation: this.state.properties.notation}
        }
      }
      return (
        <div className={cellClassName} key={key}>
          <Score entity={scoreEntity} editor={true}
            size={size} componentKey={key} componentRef={this.registerNode(key)}
            width={800}
            onInputChange={this.handleInputChange}
            onFetchComposition={this.props.onFetchComposition}
            onKeyPress={this.handleKeyPress}/>
        </div>
      )
    } else {
      // If field is hidden or read only then we maintain value in hidden field
      if (field.hidden) {
        return <input type="hidden" key={key} name={key} ref={this.registerNode(key)}
          defaultValue={value} />
      } else if (field.readOnly) {
        return (
          <div className={cellClassName} key={key}>
            <span>{value}</span>
            <input className={inputClassName}
              type="hidden" name={key} ref={this.registerNode(key)} defaultValue={value} />
          </div>
        )
      }
      var maxLength = field.maxLength || 1024
      return (
        <div className={cellClassName} key={key}>
          {field.label && <span>{field.label}</span>}
          <input name={key} type="text" className={'inline ' + inputClassName}
            placeholder={key} ref={this.registerNode(key)}
            value={value}
            onChange={this.handleInputChange}
            onKeyPress={this.handleKeyPress}
            size={size} maxLength={maxLength}
          />
        </div>
      )
    }
  }

  render() {
    if (!this.props.performance) {
      console.warn('Performance not defined ' + this.props.clazz.id + ', cannot create entity create row.')
      return <div/>
    }

    var renderedFields = this.renderInputFieldGroup(this.props.clazz, this.props.fields)
    var label = this.props.entity ? 'Update' : 'Create'

    // TODO : Etag support
    //          <input type="hidden" name="headers.Etag" ref="headers.Etag"
    //            value={this.props.entity.headers.Etag} />

    return (
      <div className="entityCreate row">
        {renderedFields}
        <div className="col-1">
          <input type="hidden" name="performance" ref={this.registerNode('performance')}
            defaultValue={this.props.performance._links.self.href} />

          {/* Provide path value for create flow so we know what type of object we are creating. */}

          {!(this.props.entity && this.props.entity._links) &&
            <input type="hidden" name="path" ref={this.registerNode('path')}
              defaultValue={this.props.clazz.path} />
          }

          {/* Provide self href for update flow so we know what to update. */}

          {this.props.entity && this.props.entity._links &&
            <div>
              <input type="hidden" name="_links.self.href" ref={this.registerNode('_links.self.href')}
                defaultValue={this.props.entity._links.self.href} />
            </div>
          }
          <button type="submit" className="btn btn-primary" onClick={this.handleApply}>{label}</button>
        </div>
      </div>
    )
  }
}

EditEntity.propTypes = {
  clazz: PropTypes.object.isRequired,
  entity: PropTypes.shape({
    _links: PropTypes.shape({
      self: PropTypes.shape({
        href: PropTypes.string.isRequired
      })
    })
  }),
  onApply: PropTypes.func.isRequired,
  onFetchComposition: PropTypes.func,
  performance: PropTypes.shape({
    _links: PropTypes.shape({
      self: PropTypes.shape({
        href: PropTypes.string.isRequired
      })
    })
  }),
  fields: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired
}

module.exports = EditEntity