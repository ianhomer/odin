const React = require('react');

const Tick = require('./tick')
const Note = require('./note')

class Pattern extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.sequence.channel}</td>
				<td>{this.props.sequence.offset}</td>
				<td>{this.props.sequence.length}</td>
				<td>{this.props.sequence.bits}</td>
				<td>{this.props.sequence.tick ?
				  <Tick
				    numerator={this.props.sequence.tick.numerator}
				    denominator={this.props.sequence.tick.denominator}
				    timeUnit={this.props.sequence.tick.timeUnit}/>
          : <div className="warn">NULL tick</div>
        }</td>
        <td>{this.props.sequence.note ?
          <Note
            number={this.props.sequence.note.number}
            velocity={this.props.sequence.note.velocity}
            duration={this.props.sequence.note.duration}/>
          : <div className="warn">NULL note</div>
        }</td>
				<td>
				  {this.props.sequence.flowName}
				</td>
			</tr>
		)
	}
}

module.exports = Pattern