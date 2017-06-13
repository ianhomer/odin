'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom')
const client = require('./client');
// end::vars[]


// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {patterns: [], channels: []};
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/patterns'}).done(response => {
			this.setState({patterns: response.entity._embedded.patterns});
		});
		client({method: 'GET', path: '/api/channels'}).done(response => {
			this.setState({channels: response.entity._embedded.channels});
		});
	}

	render() {
		return (
		  <div>
        <PatternList patterns={this.state.patterns}/>
        <ChannelList channels={this.state.channels}/>
			</div>
		)
	}
}
// end::app[]

// tag::pattern-list[]
class PatternList extends React.Component{
	render() {
		var patterns = this.props.patterns.map(pattern =>
			<Pattern key={pattern._links.self.href} pattern={pattern}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Flow Name</th>
						<th>Bits</th>
					</tr>
					{patterns}
				</tbody>
			</table>
		)
	}
}
// end::pattern-list[]

// tag::pattern[]
class Pattern extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.pattern.flowName}</td>
				<td>{this.props.pattern.bits}</td>
			</tr>
		)
	}
}
// end::pattern[]

// tag::channel-list[]
class ChannelList extends React.Component{
	render() {
		var channels = this.props.channels.map(channel =>
			<Channel key={channel._links.self.href} channel={channel}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Number</th>
						<th>Program</th>
					</tr>
					{channels}
				</tbody>
			</table>
		)
	}
}
// end::channel-list[]

// tag::channel[]
class Channel extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.channel.number}</td>
				<td>{this.props.channel.programName}</td>
			</tr>
		)
	}
}
// end::channel[]

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]