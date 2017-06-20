'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

const ChannelList = require('./components/channelList')
const PatternList = require('./components/patternList')
const ProjectList = require('./components/projectList')
const SequenceList = require('./components/sequenceList')
const Trace = require('./components/trace')

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
		  sequences: [], projects: [],  project: null, pageSize: 10,
		};
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/projects'}).done(response => {
		  var projects = response.entity._embedded.projects;
			this.setState({projects: projects, project: projects[0]});
		});
	}

	render() {
    // TODO : Switch to generic sequence list to configure more than just patterns
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
        {this.state.project &&
          <PatternList project={this.state.project}/>
        }
			</div>
		)
	}
}

ReactDOM.render(
	<App />,
	document.getElementById('react')
)
