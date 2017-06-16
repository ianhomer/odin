'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const crud = require('./crud');
const pagination = require('./pagination');

const ChannelList = require('./components/channelList')
const PatternList = require('./components/patternList')
const ProjectList = require('./components/projectList')

// end::vars[]


// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
		  patterns: [], projects: [],  project: null, pageSize: 10, links: {},
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
      client({method: 'GET', path: '/api/patterns'}).done(response => {
        this.setState({patterns: response.entity._embedded.patterns});
      });
		});
	}

	render() {
		return (
		  <div>
		    <div className="debug time"><span className="scope">app</span>{new Date().toLocaleTimeString()}</div>
		    {this.state.entities &&
		      <div className="warn">WARNING : Entities store in app state {JSON.stringify(this.state.entities)}</div>
		    }
        <ProjectList projects={this.state.projects}/>
        {this.state.project &&
          <ChannelList channels={this.state.entities}
                        project={this.state.project}
                        links={this.state.links}
                        pageSize={this.state.pageSize}
                        onCreate={crud.onCreate}
                        onDelete={crud.onDelete}
                        onNavigate={crud.onNavigate}
                        updatePageSize={this.updatePageSize}
                        attributes={this.state.attributes}/>
        }
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