const React = require('react');

class ProjectList extends React.Component{
	render() {
		var projects = this.props.projects.map(project =>
			<Project key={project._links.self.href} project={project}/>
		);
		return (
      <div>{projects}</div>
		)
	}
}

class Project extends React.Component{
	render() {
		return (
			<span>{this.props.project.name}</span>
		)
	}
}

module.exports = ProjectList