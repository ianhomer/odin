const React = require('react');

const crud = require('./../crud');
const pagination = require('./../pagination');

const Trace = require('./trace');
const Layer = require('./layer')

class LayerList extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
		  schema: [], entities: [], links: [], pageSize: 10,
		};

    crud.bindMe(this);
    pagination.bindMe(this);
	}

	componentDidMount() {
    this.loadFromServer();
	}

	render() {
    var entities = this.state.entities.map(entity =>
      <Layer key={entity._links.self.href} layer={entity} onDelete={this.onDelete}/>
    );
		return (
      <div>
        <Trace scope="layerList"/>
        <div>{entities}</div>
      </div>
		)
	}
}

LayerList.defaultProps = {
  path: 'layers'
}

module.exports = LayerList