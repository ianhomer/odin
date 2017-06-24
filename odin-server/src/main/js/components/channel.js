const React = require('react');

class Channel extends React.Component{
	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

	handleDelete() {
		this.props.onDelete(this.props.entity);
	}

	render() {
			return (
			<div className="row">
				<div className="col-2">{this.props.entity.number}</div>
				<div className="col-3">{this.props.entity.programName}</div>
				<div className="col-1">
					<button onClick={this.handleDelete}>Delete</button>
				</div>
			</div>
		)
	}
}

module.exports = Channel
