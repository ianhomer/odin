const React = require('react');

class Project extends React.Component{
	render() {
		return (
			<span>{this.props.entity.name}</span>
		)
	}
}

module.exports = Project