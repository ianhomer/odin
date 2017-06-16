const React = require('react');

// tag::project-list[]
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
// end::project-list[]

// tag::project[]
class Project extends React.Component{
	render() {
		return (
			<span>{this.props.project.name}</span>
		)
	}
}
// end::project[]

module.exports = ProjectList