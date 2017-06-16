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
		  patterns: [], entities: [], projects: [],  project: null,
		  attributes: [], pageSize: 10, links: {},
		};
		this.updatePageSize = this.updatePageSize.bind(this);
		this.onCreate = this.onCreate.bind(this);
		this.onDelete = this.onDelete.bind(this);
		this.onNavigate = this.onNavigate.bind(this);
		this.loadFromServer = this.loadFromServer.bind(this);
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/projects'}).done(response => {
		  var projects = response.entity._embedded.projects;
			this.setState({projects: projects, project: projects[0]});
  		this.loadFromServer('channels', this.state.pageSize);
      client({method: 'GET', path: '/api/patterns'}).done(response => {
        this.setState({patterns: response.entity._embedded.patterns});
      });
		});
	}

  /*
   * Load given named entities from the server.
   */
	loadFromServer(name, pageSize) {
  	follow(client, root, [
  		{rel: name, params: {size: pageSize}}]
  	).then(collection => {
  		return client({
  			method: 'GET',
  			path: collection.entity._links.profile.href,
  			headers: {'Accept': 'application/schema+json'}
  		}).then(schema => {
  			this.schema = schema.entity;
  			return collection;
  		});
  	}).done(collection => {
  		this.setState({
  			entities: collection.entity._embedded[name],
  			attributes: Object.keys(this.schema.properties),
  			pageSize: pageSize,
  			links: collection.entity._links});
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
			this.loadChannelsFromServer(this.state.pageSize);
		});
	}
	// end::create[]

	// tag::delete[]
	onDelete(entity) {
		client({method: 'DELETE', path: entity._links.self.href}).done(response => {
			this.loadFromServer('channels', this.state.pageSize);
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
			this.loadChannelsFromServer(pageSize);
		}
	}
	// end::update-page-size[]

	render() {
		return (
		  <div>
        <h1>Projects</h1>
        <ProjectList projects={this.state.projects}/>
        <ChannelList channels={this.state.entities}
                      project={this.state.project}
                      links={this.state.links}
          						pageSize={this.state.pageSize}
          						onCreate={this.onCreate}
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