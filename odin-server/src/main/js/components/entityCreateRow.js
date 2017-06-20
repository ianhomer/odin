const React = require('react');
const crud = require('./../crud');

class EntityCreateRow extends React.Component{
	constructor(props) {
		super(props);

    this.handleCreateSubmit = crud.handleCreateSubmit.bind(this);
	}

	render() {
    var fields = this.props.fields.map(name =>
      <td key={name}>
        <input type="text" placeholder={name} ref={name} className="field" />
      </td>
    );
	  return (
      <tr id="createEntity">
        {fields}
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