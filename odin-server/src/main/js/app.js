'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const crud = require('./crud');
const pagination = require('./pagination');

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
		this.updatePageSize = pagination.updatePageSize.bind(this);
		this.onCreate = crud.onCreate.bind(this);
		this.onDelete = crud.onDelete.bind(this);
		this.onNavigate = crud.onNavigate.bind(this);
		this.loadFromServer = crud.loadFromServer.bind(this);
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