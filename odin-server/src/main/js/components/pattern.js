const React = require('react');

const Tick = require('./tick')
const Note = require('./note')

class Pattern extends React.Component{
	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

  handleDelete() {
    this.props.onDelete(this.props.sequence);
  }

	render() {
		return (
			<div className="row">
				<div className="col-1">{this.props.sequence.channel}</div>
				<div className="col-1">{this.props.sequence.offset}</div>
				<div className="col-1">{this.props.sequence.length}</div>
				<div className="col-1">{this.props.sequence.bits}</div>
				<div className="col-2">
				  {this.props.sequence.tick ?
				  <Tick
				    numerator={this.props.sequence.tick.numerator}
				    denominator={this.props.sequence.tick.denominator}
				    timeUnit={this.props.sequence.tick.timeUnit}/>
          : <div className="warn">NULL tick</div>
        }</div>
        <div className="col-2">{this.props.sequence.note ?
          <Note
            number={this.props.sequence.note.number}
            velocity={this.props.sequence.note.velocity}
            duration={this.props.sequence.note.duration}/>
          : <div className="warn">NULL note</div>
        }</div>
				<div className="col-3">
				  {this.props.sequence.flowName}
				</div>
				<div className="col-1">
					<button onClick={this.handleDelete}>Delete</button>
				</div>
			</div>
		)
	}
}

module.exports = Pattern