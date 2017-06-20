const React = require('react');
const crud = require('./../crud');

class EntityCreateRow extends React.Component{
  constructor(props) {
    super(props);

    this.handleCreateSubmit = crud.handleCreateSubmit.bind(this);
	}

	render() {
	  if (!Object.keys(this.props.schema).length) {
	    console.log("WARN : Schema not defined, cannot create entity create row.")
	    return (<tr/>);
	  }

	  var fields = this.props.fields
    var renderedFields = Object.keys(fields).map(function(key) {
      var size = 0;
      var properties = this.props.schema.properties
      if (properties[key]) {
        if (properties[key].type == 'integer') {
          size = 2;
        }
      } else {
        console.log("WARN : Cannot find attribute : " + key);
      }
      return (
        <td key={key}>
          <input type="text" placeholder={key} ref={key} className="field"
            defaultValue={fields[key].defaultValue}
            size={size}
          />
        </td>
      )
    }, this);
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