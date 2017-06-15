const React = require('react');
const ReactDOM = require('react-dom');

// tag::create-dialog[]
class CreateRow extends React.Component {
	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var newChannel = {};
		this.props.attributes.forEach(attribute => {
			newChannel[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onCreate(newChannel);

		// clear out the dialog's inputs
		this.props.attributes.forEach(attribute => {
			ReactDOM.findDOMNode(this.refs[attribute]).value = '';
		});

		// Navigate away from the dialog to hide it.
		window.location = "#";
	}

  getDefaultValue(attribute) {
    if (attribute === "project") {
      return ;
    }
    return "";
  }

	render() {
		return (
      <tr id="createChannel">
        <td><input type="text" placeholder="number" ref="number" className="field" /></td>
        <td><input type="text" placeholder="programName" ref="programName" className="field" /></td>
        <td>
          <input type="hidden" name="program" ref="program"
                      value="" />
          <input type="hidden" name="project" ref="project"
                      value={this.props.project._links.self.href} />
          <button onClick={this.handleSubmit}>Create</button>
        </td>
      </tr>
		)
	}

}
// end::create-dialog[]

// tag::channel-list[]
class ChannelList extends React.Component{
	constructor(props) {
		super(props);

		this.handleNavFirst = this.handleNavFirst.bind(this);
		this.handleNavPrev = this.handleNavPrev.bind(this);
		this.handleNavNext = this.handleNavNext.bind(this);
		this.handleNavLast = this.handleNavLast.bind(this);
		this.handleInput = this.handleInput.bind(this);
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
    this.props.onNavigate(this.props.links.first.href);
  }

  handleNavPrev(e) {
    e.preventDefault();
    this.props.onNavigate(this.props.links.prev.href);
  }

  handleNavNext(e) {
    e.preventDefault();
    this.props.onNavigate(this.props.links.next.href);
  }

  handleNavLast(e) {
    e.preventDefault();
    this.props.onNavigate(this.props.links.last.href);
  }
  // end::handle-nav[]

  render() {
    var channels = this.props.channels.map(channel =>
      <Channel key={channel._links.self.href} channel={channel} onDelete={this.props.onDelete}/>
    );

    var navLinks = [];
    if ("first" in this.props.links) {
      navLinks.push(<button key="first" onClick={this.handleNavFirst}>&lt;&lt;</button>);
    }
    if ("prev" in this.props.links) {
      navLinks.push(<button key="prev" onClick={this.handleNavPrev}>&lt;</button>);
    }
    if ("next" in this.props.links) {
      navLinks.push(<button key="next" onClick={this.handleNavNext}>&gt;</button>);
    }
    if ("last" in this.props.links) {
      navLinks.push(<button key="last" onClick={this.handleNavLast}>&gt;&gt;</button>);
    }

		return (
      <div>
        <h1>Channels</h1>
        <input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/>
        <table>
          <tbody>
            <tr>
              <th>Number</th>
              <th>Program</th>
            </tr>
            {channels}
            {this.props.project &&
              <CreateRow attributes={this.props.attributes} project={this.props.project}
                onCreate={this.props.onCreate}/>
            }
          </tbody>
        </table>
				<div>
					{navLinks}
				</div>
			</div>
		)
	}
}
// end::channel-list[]

// tag::channel[]
class Channel extends React.Component{
	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

	handleDelete() {
		this.props.onDelete(this.props.channel);
	}

	render() {
			return (
			<tr>
				<td>{this.props.channel.number}</td>
				<td>{this.props.channel.programName}</td>
				<td>
					<button onClick={this.handleDelete}>Delete</button>
				</td>
			</tr>
		)
	}
}
// end::channel[]

module.exports = ChannelList