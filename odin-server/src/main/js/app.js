'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

const Trace = require('./components/trace')
const ChannelList = require('./components/channelList')
const PatternList = require('./components/patternList')
const ProjectList = require('./components/projectList')

// end::vars[]


// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
		  patterns: [], projects: [],  project: null, pageSize: 10,
		};
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
        <Trace scope="app"/>
		    {this.state.entities &&
		      <div className="warn">WARNING : Entities store in app state {JSON.stringify(this.state.entities)}</div>
		    }
        <ProjectList projects={this.state.projects}/>
        {this.state.project &&
          <ChannelList project={this.state.project}/>
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