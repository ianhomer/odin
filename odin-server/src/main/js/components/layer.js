const React = require('react');

class Layer extends React.Component{
	render() {
		return (
			<span>{this.props.layer.name}</span>
		)
	}
}

module.exports = Layer