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
const objectPath = require("object-path");
const crud = require('./../crud');

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
    this.handleKeyPress = this._handleKeyPress.bind(this);
	}

	_handleKeyPress(e) {
	  if (e.key === 'Enter') {
	    console.log("Enter -> submit");
      this.handleApply(e);
    }
	}

  renderInputFieldGroup(fields, schema, parentKey) {
    if (!fields) {
      console.log("WARN : fields not defined");
      return (<div/>)
    }
    var renderedFields = Object.keys(fields).map(function(fieldName) {
      var key = parentKey ? parentKey + "." + fieldName : fieldName;
      if (fields[fieldName].fields) {
        var cellWidth = fields[fieldName].cellWidth || 1;
        var cellClassName = "component col-" + cellWidth;
        return (
          <div className={cellClassName} key={key}>
            <div className="row">
              {this.renderInputFieldGroup(
                fields[fieldName].fields, this.getSchemaDefinition(fieldName), key)}
            </div>
          </div>
        )
      } else {
        return this.renderInputField(fields, schema, fieldName, key);
      }
    }, this);
    return renderedFields;
  }

  renderInputField(fields, schema, fieldName, key) {
    var size = 0;
    if (schema.properties[fieldName]) {
      if (schema.properties[fieldName].type == 'integer') {
        size = 3;
      }
    } else {
      console.log("WARN : Cannot find attribute : " + fieldName + " in " + JSON.stringify(properties));
    }
    var cellWidth = fields[fieldName].cellWidth || 1;
    var cellClassName = "col-" + cellWidth;
    var defaultValue;
    if (this.props.entity) {
      defaultValue = objectPath.get(this.props.entity, key);
    } else {
      defaultValue = fields[fieldName].defaultValue;
    }
    return (
      <div className={cellClassName} key={key}>
        <input type="text" placeholder={key} ref={key} className="field"
          defaultValue={defaultValue}
          onKeyPress={this.handleKeyPress}
          size={size}
        />
      </div>
    )
  }

	render() {
	  if (!Object.keys(this.props.schema).length) {
	    console.log("WARN : Schema not defined, cannot create entity create row.")
	    return (<div/>);
	  }
	  if (!this.props.project) {
	    console.log("WARN : Project not defined, cannot create entity create row.")
	    return (<div/>);
	  }

    var renderedFields = this.renderInputFieldGroup(this.props.fields, this.props.schema);
	  return (
      <div className="entityCreate row">
        {renderedFields}
        <div className="col-1">
          <input type="hidden" name="project" ref="project"
            value={this.props.project._links.self.href} />
          <button onClick={this.handleApply}>Create</button>
        </div>
      </div>
	  )
	}
}

module.exports = EditEntity