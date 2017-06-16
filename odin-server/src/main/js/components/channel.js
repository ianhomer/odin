const React = require('react');

class Channel extends React.Component{
	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

	handleDelete() {
		this.props.onDelete(this.props.channel);
	}

	render() {
			return (
			<tr>
				<td>{this.props.channel.number}</td>
				<td>{this.props.channel.programName}</td>
				<td>
					<button onClick={this.handleDelete}>Delete</button>
				</td>
			</tr>
		)
	}
}

module.exports = Channel
