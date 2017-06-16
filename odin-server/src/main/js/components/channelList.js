const React = require('react');
const ReactDOM = require('react-dom');

const crud = require('./../crud');

const Channel = require('./channel')
const CreateRow = require('./createRow')

// tag::channel-list[]
class ChannelList extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
		  attributes: [], entities: [], links: [], pageSize: 10,
		};

		this.handleNavFirst = this.handleNavFirst.bind(this);
		this.handleNavPrev = this.handleNavPrev.bind(this);
		this.handleNavNext = this.handleNavNext.bind(this);
		this.handleNavLast = this.handleNavLast.bind(this);
		this.handleInput = this.handleInput.bind(this);

		this.onCreate = crud.onCreate.bind(this);
		this.onDelete = crud.onDelete.bind(this);
		this.onNavigate = crud.onNavigate.bind(this);
		this.loadFromServer = crud.loadFromServer.bind(this);
	}

	componentDidMount() {
    this.loadFromServer('channels', this.state.pageSize);
	}

	// tag::handle-page-size-updates[]
  handleInput(e) {
    e.preventDefault();
    var pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
    if (/^[0-9]+$/.test(pageSize)) {
      this.props.updatePageSize(pageSize);
    } else {
      ReactDOM.findDOMNode(this.refs.pageSize).value =
        pageSize.substring(0, pageSize.length - 1);
    }
  }
  // end::handle-page-size-updates[]

  // tag::handle-nav[]
  handleNavFirst(e){
    e.preventDefault();
    this.props.onNavigate(this.state.links.first.href);
  }

  handleNavPrev(e) {
    e.preventDefault();
    this.props.onNavigate(this.state.links.prev.href);
  }

  handleNavNext(e) {
    e.preventDefault();
    this.props.onNavigate(this.state.links.next.href);
  }

  handleNavLast(e) {
    e.preventDefault();
    this.props.onNavigate(this.state.links.last.href);
  }
  // end::handle-nav[]

  render() {
    var entities = this.state.entities.map(channel =>
      <Channel key={channel._links.self.href} channel={channel} onDelete={this.onDelete}/>
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
		    <div className="debug time"><span className="scope">channelList</span>{new Date().toLocaleTimeString()}</div>
        <table>
          <tbody>
            <tr>
              <th>Number</th>
              <th>Program</th>
            </tr>
            {this.props.project &&
              <CreateRow attributes={this.state.attributes} project={this.props.project}
                onCreate={this.onCreate}/>
            }
            {entities}
          </tbody>
        </table>
				<div>
          page size : <input ref="pageSize" defaultValue={this.state.pageSize} onInput={this.handleInput}/>
					{navLinks}
				</div>
			</div>
		)
	}
}
ChannelList.defaultProps = {
  path: 'channels'
}
// end::channel-list[]

module.exports = ChannelList