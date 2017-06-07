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
		this.state = {patterns: []};
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/patterns'}).done(response => {
			this.setState({patterns: response.entity._embedded.patterns});
		});
	}

	render() {
		return (
			<PatternList patterns={this.state.patterns}/>
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
						<th>Class</th>
						<th>Pattern</th>
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
				<td>{this.props.pattern.className}</td>
				<td>{this.props.pattern.patternAsInt}</td>
			</tr>
		)
	}
}
// end::pattern[]

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]