const React = require('react');

class Note extends React.Component{
	constructor(props) {
		super(props);
	}

	render() {
	  return (
	    <table className="note">
        <tbody>
          <tr>
            <td>{this.props.number}</td>
            <td>{this.props.velocity}</td>
            <td>{this.props.duration}</td>
          </tr>
        </tbody>
      </table>
    )
	}
}

module.exports = Note



