const React = require('react');

const EditEntity = require('./editEntity')
const Note = require('./note')
const Tick = require('./tick')

/**
 * Pattern component.
 */
class Pattern extends React.Component{
	constructor(props) {
		super(props);

    this.state = {};

		this.handleDelete = this.handleDelete.bind(this);
		this.toggleEditing = this._toggleEditing.bind(this);
		this.onApplySuccess = this._onApplySuccess.bind(this);
	}

  _toggleEditing(e) {
    console.log("Editing : " + this.props.entity._links.self.href);
    this.setState({editing : this.props.entity._links.self.href});
  }

  _onApplySuccess(e) {
    console.log("On apply success");
    this.setState({editing : null});
  }

  handleDelete() {
    this.props.onDelete(this.props.entity);
  }

	render() {
	  var sequence = this.props.entity;
	  if (this.state.editing) {
      return (
        /*
         * Edit entity
         */
        <EditEntity entity={sequence}
          project={this.props.project}
          path={this.props.path} fields={this.props.fields} schema={this.props.schema}
          onApply={this.onApplySuccess}
          onApplySuccess={this.onApplySuccess}
          />
      )
	  } else {
      return (
        /*
         * View entity
         */
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
          <div className="col-2">{sequence.note ?
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