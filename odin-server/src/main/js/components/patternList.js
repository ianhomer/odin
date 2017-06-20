const React = require('react');

const crud = require('./../crud');
const pagination = require('./../pagination');

const Pattern = require('./pattern')
const EntityCreateRow = require('./entityCreateRow')
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

  render() {
    var entities = this.state.entities.map(entity =>
      <Pattern key={entity._links.self.href} sequence={entity} onDelete={this.onDelete}/>
    );
		return (
		  <div>
        <Trace scope={this.path}/>
        <table>
          <tbody>
            <tr>
              <th>Channel</th>
              <th>Offset</th>
              <th>Length</th>
              <th>Bits</th>
              <th>Tick</th>
              <th>Note</th>
              <th>Flow Name</th>
            </tr>
            <EntityCreateRow project={this.props.project} onCreate={this.onCreate}
              fields={this.props.fields} schema={this.state.schema}/>
            {entities}
          </tbody>
        </table>
      </div>
		)
	}
}

PatternList.defaultProps = {
  path: 'patterns',
  fields: {
    'channel' : { defaultValue : 1},
    'offset' : { defaultValue : -1},
    'length' : {defaultValue : 0},
    'bits' : {defaultValue : 1},
    'tick' : {
      fields : {
        'numerator' : {defaultValue : 1},
        'denominator' : {defaultValue : 1},
        'timeUnit' : {defaultValue : "BEAT"},
      }
    },
    'note' : {
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