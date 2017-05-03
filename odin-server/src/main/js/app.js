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
		this.state = {sequences: []};
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/sequence'}).done(response => {
			this.setState({sequences: response.entity._embedded.sequences});
		});
	}

	render() {
		return (
			<SequenceList series={this.state.series}/>
		)
	}
}
// end::app[]

// tag::sequence-list[]
class SequenceList extends React.Component{
	render() {
		var sequences = this.props.series.map(sequence =>
			<Sequence key={sequence._links.self.href} sequence={sequence}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Class</th>
					</tr>
					{sequences}
				</tbody>
			</table>
		)
	}
}
// end::sequence-list[]

// tag::sequence[]
class Sequence extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.sequence.className}</td>
			</tr>
		)
	}
}
// end::sequence[]

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]