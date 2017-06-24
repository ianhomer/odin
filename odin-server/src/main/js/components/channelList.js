const React = require('react');
const ReactDOM = require('react-dom');

const crud = require('./../crud');
const pagination = require('./../pagination');

const Trace = require('./trace');
const Channel = require('./channel')
const EditEntity = require('./editEntity')

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
      <Channel entity={entity}
        path={this.props.path} schema={this.state.schema}
        key={entity._links.self.href}
        onDelete={this.onDelete}/>
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
        <div className="container">
          <div className="row">
            <div className="col-2">Number</div>
            <div className="col-3">Program</div>
          </div>
          <EditEntity
            project={this.props.project}
            path={this.props.path} fields={this.props.fields} schema={this.state.schema}
            onApply={this.onCreate}
            />
          {entities}
        </div>
				<div>
          page size :
          <input ref="pageSize" defaultValue={this.state.pageSize} onInput={this.handlePageSizeInput}/>
					{navLinks}
				</div>
			</div>
		)
	}
}

ChannelList.defaultProps = {
  path: 'channels',
  fields: {
    'number' : {
      defaultValue : '1',
      cellWidth : 2
    },
    'programName' : {
      defaultValue : 'piano',
      cellWidth : 3
    }
  }
}

module.exports = ChannelList