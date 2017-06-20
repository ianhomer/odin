const React = require('react');

class Tick extends React.Component{
	constructor(props) {
		super(props);
	}

	render() {
	  return (
	    <table className="tick">
        <tbody>
          <tr>
            <td>{(this.props.denominator > 1 || this.props.numerator > 1) &&
                  <span className="numerator">{this.props.numerator}</span>
                }</td>
            <td>{this.props.denominator > 1 &&
                  <span className="denominator">/{this.props.denominator}</span>
                }</td>
            <td>{this.props.timeUnit}</td>
          </tr>
        </tbody>
      </table>
    )
	}
}

module.exports = Tick