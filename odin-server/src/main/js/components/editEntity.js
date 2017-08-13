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

'use strict';

const React = require('react');
const objectPath = require('object-path');
const crud = require('./../crud');
const Score = require('./score');

// Edit an entity.
class EditEntity extends React.Component{
  constructor(props) {
    super(props);

    this.handleApply = crud.handleApply.bind(this);
    if (this.props.entity) {
      this.onApply = crud.onUpdate.bind(this);
    } else {
      this.onApply = crud.onCreate.bind(this);
    }
    this.getSchemaDefinition = crud.getSchemaDefinition.bind(this);
    this.getSchema = crud.getSchema.bind(this);
    this.handleKeyPress = this._handleKeyPress.bind(this);
  }

  _handleKeyPress(e) {
    if (e.key === 'Enter') {
      this.handleApply(e);
    }
  }

  // Render a group of inputs for the specified fields.
  renderInputFieldGroup(fields, schema, parentKey) {
    if (!fields) {
      console.warn('Fields not defined');
      return (<div/>);
    }
    if (!schema) {
      console.warn('Schema not defined');
      return (<div/>);
    }
    if (!schema.properties) {
      console.warn('Schema does not have properties');
      return (<div>{JSON.stringify(schema)}</div>);
    }
    var renderedFields = Object.keys(fields).map(function(fieldName) {
      var key = parentKey ? parentKey + '.' + fieldName : fieldName;
      if (fields[fieldName].fields) {
        // Given field is a group of child fields
        var cellWidth = fields[fieldName].cellWidth || 1;
        var cellClassName = 'component col-' + cellWidth;
        return (
          <div className={cellClassName} key={key}>
            <div className="row">
              {this.renderInputFieldGroup(
                fields[fieldName].fields, this.getSchemaDefinition(fieldName), key)}
            </div>
          </div>
        );
      } else {
        return this.renderInputField(fields, schema, fieldName, key);
      }
    }, this);
    return renderedFields;
  }

  renderInputField(fields, schema, fieldName, key) {
    var field = fields[fieldName];
    var size = 0;
    if (field.size) {
      size = field.size;
    } else {
      if (schema.properties[fieldName]) {
        if (schema.properties[fieldName].type == 'integer') {
          size = 3;
        }
      } else {
        console.error('Cannot find attribute : ' + fieldName + ' in ' + JSON.stringify(schema.properties));
      }
    }
    var cellWidth = field.cellWidth || 1;
    var cellClassName = 'col-' + cellWidth;
    var value;
    if (this.props.entity) {
      value = objectPath.get(this.props.entity, key);
    } else {
      value = field.defaultValue;
    }
    if (fieldName == 'notation') {
      var scoreEntity = this.props.entity ?
        this.props.entity :
        { notation : value };
      return (
        <div className={cellClassName} key={key}>
          <Score entity={scoreEntity} editor="true"
            size={size} componentKey={key} componentRef={el => this.refs[key] = el}
            onKeyPress={this.handleKeyPress}/>
        </div>
      );
    } else {
      // If field is hidden or read only then we maintain value in hidden field
      if (field.hidden) {
        return (<input type="hidden" key={key} name={key} ref={key} value={value} />);
      } else if (field.readOnly) {
        return (
          <div className={cellClassName} key={key}>
            <span>{value}</span>
            <input type="hidden" name={key} ref={key} value={value} />
          </div>
        );
      }
      var maxLength = field.maxLength || 1024;
      return (
        <div className={cellClassName} key={key}>
          {field.label && <span>{field.label}</span>}
          <input type="text" placeholder={key} ref={key} className="inline"
            defaultValue={value}
            onKeyPress={this.handleKeyPress}
            size={size} maxLength={maxLength}
          />
        </div>
      );
    }
  }

  render() {
    if (!Object.keys(this.getSchema()).length) {
      console.warn('Schema not defined for ' + this.getSchema() + ', cannot create entity create row.');
      return (<div/>);
    }
    if (!this.props.project) {
      console.warn('Project not defined ' + this.props.path + ', cannot create entity create row.');
      return (<div/>);
    }

    var renderedFields = this.renderInputFieldGroup(this.props.fields, this.getSchema());
    var label = this.props.entity ? 'Update' : 'Create';

    // TODO : Etag support
    //          <input type="hidden" name="headers.Etag" ref="headers.Etag"
    //            value={this.props.entity.headers.Etag} />

    return (
      <div className="entityCreate row">
        {renderedFields}
        <div className="col-1">
          <input type="hidden" name="project" ref="project"
            value={this.props.project._links.self.href} />

          {/* Provide path value for create flow so we know what type of object we are creating. */}

          {!this.props.entity &&
            <input type="hidden" name="path" ref="path"
              value={this.props.path} />
          }

          {/* Provide self href for update flow so we know what to update. */}

          {this.props.entity &&
            <div>
              <input type="hidden" name="_links.self.href" ref="_links.self.href"
                value={this.props.entity._links.self.href} />
            </div>
          }
          <button type="submit" className="btn btn-primary" onClick={this.handleApply}>{label}</button>
        </div>
      </div>
    );
  }
}

module.exports = EditEntity;