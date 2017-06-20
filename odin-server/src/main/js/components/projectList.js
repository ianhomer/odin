const React = require('react');

const Project = require('./project')

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

module.exports = ProjectList