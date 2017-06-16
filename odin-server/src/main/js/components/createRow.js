const React = require('react');
const ReactDOM = require('react-dom');

class CreateRow extends React.Component {
	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var newChannel = {};
		this.props.attributes.forEach(attribute => {
			newChannel[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onCreate(newChannel);

		// clear out the dialog's inputs
		this.props.attributes.forEach(attribute => {
			ReactDOM.findDOMNode(this.refs[attribute]).value = '';
		});

		// Navigate away from the dialog to hide it.
		window.location = "#";
	}

  getDefaultValue(attribute) {
    if (attribute === "project") {
      return ;
    }
    return "";
  }

	render() {
		return (
      <tr id="createChannel">
        <td><input type="text" placeholder="number" ref="number" className="field" /></td>
        <td><input type="text" placeholder="programName" ref="programName" className="field" /></td>
        <td>
          <input type="hidden" name="program" ref="program"
                      value="" />
          <input type="hidden" name="project" ref="project"
                      value={this.props.project._links.self.href} />
          <button onClick={this.handleSubmit}>Create</button>
        </td>
      </tr>
		)
	}
}

module.exports = CreateRow
