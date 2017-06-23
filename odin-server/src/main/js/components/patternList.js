const React = require('react');

const crud = require('./../crud');
const pagination = require('./../pagination');

const Pattern = require('./pattern')
const EditEntity = require('./editEntity')
const Trace = require('./trace');

class PatternList extends React.Component{
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

  /*
   * Render patterns.  Note that as per bootstrap requirement element sizes in a row MUST not
   * add up to greater than 12 - see https://v4-alpha.getbootstrap.com/layout/grid/ .
   */
  render() {
    var entities = this.state.entities.map(entity =>
      <Pattern key={entity._links.self.href} sequence={entity} onDelete={this.onDelete}/>
    );
		return (
		  <div>
        <Trace scope={this.path}/>
        <div className="container">
          <div className="row">
            <div className="col-1">Channel</div>
            <div className="col-1">Offset</div>
            <div className="col-1">Length</div>
            <div className="col-1">Bits</div>
            <div className="col-2">Tick</div>
            <div className="col-2">Note</div>
            <div className="col-3">Flow Name</div>
          </div>
          <EditEntity
            onCreate={this.onCreate} project={this.props.project}
            path={this.props.path} fields={this.props.fields} schema={this.state.schema}
            />
          {entities}
        </div>
      </div>
		)
	}
}

PatternList.defaultProps = {
  path: 'patterns',
  fields: {
    'channel' : { defaultValue : 1},
    'offset' : { defaultValue : 0},
    'length' : {defaultValue : -1},
    'bits' : {defaultValue : 1},
    'tick' : {
      cellWidth : 2,
      fields : {
        'numerator' : {defaultValue : 1},
        'denominator' : {defaultValue : 1},
        'timeUnit' : {defaultValue : "BEAT"},
      }
    },
    'note' : {
      cellWidth : 2,
      fields : {
        'number' : {defaultValue : 60},
        'velocity' : {defaultValue : 100},
        'duration' : {defaultValue : 1}
      }
    },
    'flowName' : {defaultValue : "com.purplepip.odin.music.flow.PatternFlow"}
  },
}

module.exports = PatternList