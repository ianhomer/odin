var React = require('react');

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
						<th>Channel</th>
						<th>Bits</th>
						<th>Tick</th>
						<th>Flow Name</th>
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
				<td>{this.props.pattern.channel}</td>
				<td>{this.props.pattern.bits}</td>
				<td>{this.props.pattern.tick}</td>
				<td>{this.props.pattern.flowName}</td>
			</tr>
		)
	}
}
// end::pattern[]

module.exports = PatternList