const React = require('react');

class Layer extends React.Component{
	render() {
		return (
			<span>{this.props.entity.name}</span>
		)
	}
}

module.exports = Layer