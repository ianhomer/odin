const React = require('react');

class Sequence extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.sequence.channel}</td>
				<td>{this.props.sequence.bits}</td>
				<td>{this.props.sequence.tick}</td>
				<td>{this.props.sequence.flowName}</td>
			</tr>
		)
	}
}

module.exports = Sequence