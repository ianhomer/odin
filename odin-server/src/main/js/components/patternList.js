const React = require('react');

const crud = require('./../crud');
const pagination = require('./../pagination');

const Pattern = require('./pattern')
const Trace = require('./trace');

class PatternList extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
		  attributes: [], entities: [], links: [], pageSize: 10,
		};

		this.onCreate = crud.onCreate.bind(this);
		this.onDelete = crud.onDelete.bind(this);
		this.loadFromServer = crud.loadFromServer.bind(this);

		this.updatePageSize = pagination.updatePageSize.bind(this);
		this.onNavigate = pagination.onNavigate.bind(this);
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
              <th>Velocity</th>
              <th>Duration</th>
              <th>Flow Name</th>
            </tr>
            {entities}
          </tbody>
        </table>
      </div>
		)
	}
}

PatternList.defaultProps = {
  path: 'patterns'
}

module.exports = PatternList