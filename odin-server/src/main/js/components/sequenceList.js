const React = require('react');

const crud = require('./../crud');
const pagination = require('./../pagination');

const Sequence = require('./sequence')
const Trace = require('./trace');

/**
 * Rendering of generic sequence list - NOT CURRENTLY USED.
 */
class SequenceList extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
		  attributes: [], entities: [], links: [], pageSize: 10,
		};

		crud.bindMe(this);
	}

  componentDidMount() {
    this.loadFromServer();
  }

  render() {
    var entities = this.state.entities.map(entity =>
      <Sequence key={entity._links.self.href} sequence={entity} onDelete={this.onDelete}/>
    );
		return (
		  /*
		   * View sequence list.
		   */
		  <div>
        <Trace scope="sequenceList"/>
        <table>
          <tbody>
            <tr>
              <th>Channel</th>
              <th>Bits</th>
              <th>Tick</th>
              <th>Flow Name</th>
            </tr>
            {entities}
          </tbody>
        </table>
      </div>
		)
	}
}

SequenceList.defaultProps = {
  path: 'sequences'
}

module.exports = SequenceList