const React = require('react');

class Project extends React.Component{
	render() {
		return (
			<span>{this.props.project.name}</span>
		)
	}
}

module.exports = Project