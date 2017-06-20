const React = require('react');
const crud = require('./../crud');

class EntityCreateRow extends React.Component{
  constructor(props) {
    super(props);

    this.handleCreateSubmit = crud.handleCreateSubmit.bind(this);
	}

  renderInputFieldGroup(fields, properties, parentKey) {
    // TODO : Need to deference schema to inspect properties of embedded objects.
    var renderedFields = Object.keys(fields).map(function(fieldName) {
      var key = parentKey ? parentKey + "." + fieldName : fieldName;
      if (fields[fieldName].fields) {
        return (
          <td key={key}>
            <table>
              <tbody>
                <tr>
                {this.renderInputFieldGroup(fields[fieldName].fields, properties, key)}
                </tr>
              </tbody>
            </table>
          </td>
        )
      } else {
        return this.renderInputField(fields, properties, fieldName, key);
      }
    }, this);
    return renderedFields;
  }

  renderInputField(fields, properties, fieldName, key) {
    var size = 0;
    if (properties[key]) {
      if (properties[key].type == 'integer') {
        size = 2;
      }
    } else {
      console.log("WARN : Cannot find attribute : " + key);
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

    var renderedFields = this.renderInputFieldGroup(this.props.fields, this.props.schema.properties);
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