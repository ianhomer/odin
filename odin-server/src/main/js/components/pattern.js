const React = require('react');

const EditEntity = require('./editEntity')
const Note = require('./note')
const Tick = require('./tick')

class Pattern extends React.Component{
	constructor(props) {
		super(props);

		this.state = {
		  sequence : this.props.sequence
		};
		this.handleDelete = this.handleDelete.bind(this);
		this.toggleEditing = this._toggleEditing.bind(this);
		this.onUpdate = this._onUpdate.bind(this);
		this.container = this.props.container;
	}

  _toggleEditing(e) {
    console.log("Editing : " + this.state.sequence._links.self.href);
    this.setState({editing : this.state.sequence._links.self.href});
  }

  _onUpdate(e) {
    console.log("TODO on Update");
    this.setState({editing : null});
  }

  handleDelete() {
    this.props.onDelete(this.props.sequence);
  }

	render() {
	  var sequence = this.state.sequence;
	  if (this.state.editing) {
      return (
        <EditEntity
          onApply={this.onUpdate}
          project={this.container.props.project} entity={sequence}
          path={this.container.props.path} fields={this.container.props.fields}
          schema={this.container.state.schema}
          />
      )
	  } else {
      return (
        <div className="row" onClick={this.toggleEditing}>
          <div className="col-1">{sequence.channel}</div>
          <div className="col-1">{sequence.offset}</div>
          <div className="col-1">{sequence.length}</div>
          <div className="col-1">{sequence.bits}</div>
          <div className="col-2">
            {sequence.tick ?
            <Tick
              numerator={sequence.tick.numerator}
              denominator={sequence.tick.denominator}
              timeUnit={sequence.tick.timeUnit}/>
            : <div className="warn">NULL tick</div>
          }</div>
          <div className="col-2">{this.props.sequence.note ?
            <Note
              number={sequence.note.number}
              velocity={sequence.note.velocity}
              duration={sequence.note.duration}/>
            : <div className="warn">NULL note</div>
          }</div>
          <div className="col-3">
            {sequence.flowName}
          </div>
          <div className="col-1">
            <button onClick={this.handleDelete}>Delete</button>
          </div>
        </div>
      )
		}
	}
}

module.exports = Pattern