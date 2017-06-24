const React = require('react');

/**
 * Trace component to handle information for development support.
 */
class Trace extends React.Component{
	constructor(props) {
		super(props);
	}

	render() {
			return (
		    <div className="debug time"><span className="scope">{this.props.scope}</span>{new Date().toLocaleTimeString()}</div>
		)
	}
}

module.exports = Trace