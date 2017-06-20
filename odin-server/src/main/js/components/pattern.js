const React = require('react');

class Pattern extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.sequence.channel}</td>
				<td>{this.props.sequence.offset}</td>
				<td>{this.props.sequence.length}</td>
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
				<td>{this.props.sequence.note.number}</td>
				<td>{this.props.sequence.note.velocity}</td>
				<td>{this.props.sequence.note.duration}</td>
				<td>
				  {this.props.sequence.flowName}
				</td>
			</tr>
		)
	}
}

module.exports = Pattern