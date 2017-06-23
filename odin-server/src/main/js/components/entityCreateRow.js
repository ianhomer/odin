const React = require('react');
const crud = require('./../crud');

/**
 * Table row to create an entity
 */
class EntityCreateRow extends React.Component{
  constructor(props) {
    super(props);

    this.handleCreateSubmit = crud.handleCreateSubmit.bind(this);
    this.getSchemaDefinition = crud.getSchemaDefinition.bind(this);
	}

  renderInputFieldGroup(fields, schema, parentKey) {
    // TODO : Need to deference schema to inspect properties of embedded objects.
    var renderedFields = Object.keys(fields).map(function(fieldName) {
      var key = parentKey ? parentKey + "." + fieldName : fieldName;
      if (fields[fieldName].fields) {
        return (
          <td key={key}>
            <table>
              <tbody>
                <tr>
                {this.renderInputFieldGroup(
                  fields[fieldName].fields, this.getSchemaDefinition(fieldName), key)}
                </tr>
              </tbody>
            </table>
          </td>
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
    console.log("Rendering : " + key);
    return (
      <td key={key}>
        <input type="text" placeholder={key} ref={key} className="field"
          defaultValue={fields[fieldName].defaultValue}
          size={size}
        />
      </td>
    )
  }

	render() {
	  if (!Object.keys(this.props.schema).length) {
	    console.log("WARN : Schema not defined, cannot create entity create row.")
	    return (<tr/>);
	  }

    var renderedFields = this.renderInputFieldGroup(this.props.fields, this.props.schema);
	  return (
      <tr id="createEntity">
        {renderedFields}
        <td>
          <div>
            <input type="hidden" name="project" ref="project"
              value={this.props.project._links.self.href} />
            <button onClick={this.handleCreateSubmit}>Create</button>
          </div>
        </td>
      </tr>
	  )
	}
}

module.exports = EntityCreateRow