var React = require('react');

// tag::project-list[]
class ProjectList extends React.Component{
	render() {
		var projects = this.props.projects.map(project =>
			<Project key={project._links.self.href} project={project}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Name</th>
					</tr>
					{projects}
				</tbody>
			</table>
		)
	}
}
// end::project-list[]

// tag::project[]
class Project extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.project.name}</td>
			</tr>
		)
	}
}
// end::project[]

module.exports = ProjectList