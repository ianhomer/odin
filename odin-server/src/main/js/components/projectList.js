const React = require('react');

const Project = require('./project')

class ProjectList extends React.Component{
	render() {
		var projects = this.props.projects.map(project =>
			<Project entity={project}
			  key={project._links.self.href} />
		);
		return (
      <div>{projects}</div>
		)
	}
}

module.exports = ProjectList