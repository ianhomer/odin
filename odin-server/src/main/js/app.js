'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const follow = require('./follow');
const root = '/api';
const ChannelList = require('./model/channelList')
const PatternList = require('./model/patternList')
const ProjectList = require('./model/projectList')
// end::vars[]


// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
		  patterns: [], channels: [], projects: [],
		  attributes: [], pageSize: 5, links: {}
		};
		this.updatePageSize = this.updatePageSize.bind(this);
		this.onCreate = this.onCreate.bind(this);
		this.onDelete = this.onDelete.bind(this);
		this.onNavigate = this.onNavigate.bind(this);
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/projects'}).done(response => {
			this.setState({projects: response.entity._embedded.projects});
		});
		client({method: 'GET', path: '/api/patterns'}).done(response => {
			this.setState({patterns: response.entity._embedded.patterns});
		});
		this.loadFromServer(this.state.pageSize);
	}

	loadFromServer(pageSize) {
  	follow(client, root, [
  		{rel: 'channels', params: {size: pageSize}}]
  	).then(channelCollection => {
  		return client({
  			method: 'GET',
  			path: channelCollection.entity._links.profile.href,
  			headers: {'Accept': 'application/schema+json'}
  		}).then(schema => {
  			this.schema = schema.entity;
  			return channelCollection;
  		});
  	}).done(channelCollection => {
  		this.setState({
  			channels: channelCollection.entity._embedded.channels,
  			attributes: Object.keys(this.schema.properties),
  			pageSize: pageSize,
  			links: channelCollection.entity._links});
  	});
  }

	// tag::create[]
	onCreate(newChannel) {
		follow(client, root, ['channels']).then(channelCollection => {
			return client({
				method: 'POST',
				path: channelCollection.entity._links.self.href,
				entity: newChannel,
				headers: {'Content-Type': 'application/json'}
			})
		}).then(response => {
			return follow(client, root, [
				{rel: 'channels', params: {'size': this.state.pageSize}}]);
		}).done(response => {
			if (typeof response.entity._links.last != "undefined") {
				this.onNavigate(response.entity._links.last.href);
			} else {
				this.onNavigate(response.entity._links.self.href);
			}
		});
	}
	// end::create[]

	// tag::delete[]
	onDelete(employee) {
		client({method: 'DELETE', path: employee._links.self.href}).done(response => {
			this.loadFromServer(this.state.pageSize);
		});
	}
	// end::delete[]

	// tag::navigate[]
	onNavigate(navUri) {
		client({method: 'GET', path: navUri}).done(employeeCollection => {
			this.setState({
				employees: employeeCollection.entity._embedded.employees,
				attributes: this.state.attributes,
				pageSize: this.state.pageSize,
				links: employeeCollection.entity._links
			});
		});
	}
	// end::navigate[]

	// tag::update-page-size[]
	updatePageSize(pageSize) {
		if (pageSize !== this.state.pageSize) {
			this.loadFromServer(pageSize);
		}
	}
	// end::update-page-size[]

	render() {
		return (
		  <div>
        <h1>Projects</h1>
        <ProjectList projects={this.state.projects}/>
        <ChannelList channels={this.state.channels}
                      links={this.state.links}
          						pageSize={this.state.pageSize}
          						onNavigate={this.onNavigate}
          						onDelete={this.onDelete}
          						updatePageSize={this.updatePageSize}
          						attributes={this.state.attributes}/>
        <h1>Patterns</h1>
        <PatternList patterns={this.state.patterns}/>
			</div>
		)
	}
}
// end::app[]

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]