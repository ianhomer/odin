const React = require('react');

class Sequence extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.sequence.channel}</td>
				<td>{this.props.sequence.bits}</td>
				<td>
				  <div className="tick">
				    {(this.props.sequence.tick.denominator > 1 || this.props.sequence.tick.numerator > 1) &&
              <span className="numerator">{this.props.sequence.tick.numerator}</span>
            }
            {this.props.sequence.tick.denominator > 1 &&
              <span className="denominator">/{this.props.sequence.tick.denominator}</span>
            }
				    &nbsp;{this.props.sequence.tick.timeUnit}
				  </div>
				</td>
				<td>{this.props.sequence.flowName}</td>
			</tr>
		)
	}
}

module.exports = Sequence