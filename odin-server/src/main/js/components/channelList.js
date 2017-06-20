const React = require('react');
const ReactDOM = require('react-dom');

const crud = require('./../crud');
const pagination = require('./../pagination');

const Trace = require('./trace');
const Channel = require('./channel')
const EntityCreateRow = require('./entityCreateRow')

class ChannelList extends React.Component{
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
      <Channel key={entity._links.self.href} channel={entity} onDelete={this.onDelete}/>
    );

    var navLinks = [];
    if ("first" in this.state.links) {
      navLinks.push(<button key="first" onClick={this.handleNavFirst}>&lt;&lt;</button>);
    }
    if ("prev" in this.state.links) {
      navLinks.push(<button key="prev" onClick={this.handleNavPrev}>&lt;</button>);
    }
    if ("next" in this.state.links) {
      navLinks.push(<button key="next" onClick={this.handleNavNext}>&gt;</button>);
    }
    if ("last" in this.state.links) {
      navLinks.push(<button key="last" onClick={this.handleNavLast}>&gt;&gt;</button>);
    }

		return (
      <div>
        <Trace scope="channelList"/>
        <table>
          <tbody>
            <tr>
              <th>Number</th>
              <th>Program</th>
            </tr>
            <EntityCreateRow project={this.props.project} onCreate={this.onCreate}
              fields={this.props.fields} schema={this.state.schema}/>
            {entities}
          </tbody>
        </table>
				<div>
          page size : <input ref="pageSize" defaultValue={this.state.pageSize} onInput={this.handlePageSizeInput}/>
					{navLinks}
				</div>
			</div>
		)
	}
}

ChannelList.defaultProps = {
  path: 'channels',
  fields: {
    'number' : { defaultValue : '1'},
    'programName' : { defaultValue : 'piano'}
  }
}

module.exports = ChannelList