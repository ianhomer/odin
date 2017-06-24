const React = require('react');

/**
 * Tick component.
 */
class Tick extends React.Component{
	constructor(props) {
		super(props);
	}

	render() {
	  return (
  	  <div className="component row">
        <div className="col-md-1">{(this.props.denominator > 1 || this.props.numerator > 1) &&
              <span className="numerator">{this.props.numerator}</span>
            }</div>
        <div className="col-md-1">{this.props.denominator > 1 &&
              <span className="denominator">/{this.props.denominator}</span>
            }</div>
        <div className="col-md-1">{this.props.timeUnit}</div>
      </div>
    )
	}
}

module.exports = Tick