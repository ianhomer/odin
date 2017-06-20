const React = require('react');
const ReactDOM = require('react-dom');

const crud = require('./../crud');
const pagination = require('./../pagination');

const Trace = require('./trace');
const Channel = require('./channel')

class ChannelList extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
		  attributes: [], entities: [], links: [], pageSize: 10,
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
            <tr id="createEntity">
              <td>
                <input type="text" placeholder="number" ref="number" className="field" />
              </td>
              <td>
                <input type="text" placeholder="programName" ref="programName" className="field" />
                <input type="hidden" name="program" ref="program" value="" />
                <input type="hidden" name="project" ref="project"
                  value={this.props.project._links.self.href} />
              </td>
              <td>
                <button onClick={this.handleCreateSubmit}>Create</button>
              </td>
            </tr>
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
  path: 'channels'
}

module.exports = ChannelList