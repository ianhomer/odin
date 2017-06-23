const React = require('react');

class Note extends React.Component{
	constructor(props) {
		super(props);
	}

	render() {
	  return (
  	  <div className="component row">
        <div className="col-1">{this.props.number}</div>
        <div className="col-1">{this.props.velocity}</div>
        <div className="col-1">{this.props.duration}</div>
      </div>
    )
	}
}

module.exports = Note



