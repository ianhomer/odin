'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom')
const client = require('./client');
const follow = require('./follow');
const root = '/api';
// end::vars[]


// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {patterns: [], channels: [], attributes: [], pageSize: 5, links: {}};
		this.updatePageSize = this.updatePageSize.bind(this);
		this.onCreate = this.onCreate.bind(this);
		this.onDelete = this.onDelete.bind(this);
		this.onNavigate = this.onNavigate.bind(this);
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/patterns'}).done(response => {
			this.setState({patterns: response.entity._embedded.patterns});
		});
		this.loadFromServer(this.state.pageSize);
	}

	loadFromServer(pageSize) {
  	follow(client, root, [
  		{rel: 'channels', params: {size: pageSize}}]
  	).then(channelCollection => {
  		return client({
  			method: 'GET',
  			path: channelCollection.entity._links.profile.href,
  			headers: {'Accept': 'application/schema+json'}
  		}).then(schema => {
  			this.schema = schema.entity;
  			return channelCollection;
  		});
  	}).done(channelCollection => {
  		this.setState({
  			channels: channelCollection.entity._embedded.channels,
  			attributes: Object.keys(this.schema.properties),
  			pageSize: pageSize,
  			links: channelCollection.entity._links});
  	});
  }

	// tag::create[]
	onCreate(newChannel) {
		follow(client, root, ['channels']).then(channelCollection => {
			return client({
				method: 'POST',
				path: channelCollection.entity._links.self.href,
				entity: newChannel,
				headers: {'Content-Type': 'application/json'}
			})
		}).then(response => {
			return follow(client, root, [
				{rel: 'channels', params: {'size': this.state.pageSize}}]);
		}).done(response => {
			if (typeof response.entity._links.last != "undefined") {
				this.onNavigate(response.entity._links.last.href);
			} else {
				this.onNavigate(response.entity._links.self.href);
			}
		});
	}
	// end::create[]

	// tag::delete[]
	onDelete(employee) {
		client({method: 'DELETE', path: employee._links.self.href}).done(response => {
			this.loadFromServer(this.state.pageSize);
		});
	}
	// end::delete[]

	// tag::navigate[]
	onNavigate(navUri) {
		client({method: 'GET', path: navUri}).done(employeeCollection => {
			this.setState({
				employees: employeeCollection.entity._embedded.employees,
				attributes: this.state.attributes,
				pageSize: this.state.pageSize,
				links: employeeCollection.entity._links
			});
		});
	}
	// end::navigate[]

	// tag::update-page-size[]
	updatePageSize(pageSize) {
		if (pageSize !== this.state.pageSize) {
			this.loadFromServer(pageSize);
		}
	}
	// end::update-page-size[]

	render() {
		return (
		  <div>
		    <CreateDialog attributes={this.state.attributes} onCreate={this.onCreate}/>
		    <h1>Channels</h1>
        <ChannelList channels={this.state.channels}
                      links={this.state.links}
          						pageSize={this.state.pageSize}
          						onNavigate={this.onNavigate}
          						onDelete={this.onDelete}
          						updatePageSize={this.updatePageSize}/>
        <h1>Patterns</h1>
        <PatternList patterns={this.state.patterns}/>
			</div>
		)
	}
}
// end::app[]

// tag::create-dialog[]
class CreateDialog extends React.Component {

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

	render() {
		var inputs = this.props.attributes.map(attribute =>
			<p key={attribute}>
				<input type="text" placeholder={attribute} ref={attribute} className="field" />
			</p>
		);

		return (
			<div>
				<a href="#createChannel">Create</a>

				<div id="createChannel" className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Create new channel</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Create</button>
						</form>
					</div>
				</div>
			</div>
		)
	}

}
// end::create-dialog[]

// tag::pattern-list[]
class PatternList extends React.Component{
	render() {
		var patterns = this.props.patterns.map(pattern =>
			<Pattern key={pattern._links.self.href} pattern={pattern}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Channel</th>
						<th>Bits</th>
						<th>Tick</th>
						<th>Flow Name</th>
					</tr>
					{patterns}
				</tbody>
			</table>
		)
	}
}
// end::pattern-list[]

// tag::pattern[]
class Pattern extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.pattern.channel}</td>
				<td>{this.props.pattern.bits}</td>
				<td>{this.props.pattern.tick}</td>
				<td>{this.props.pattern.flowName}</td>
			</tr>
		)
	}
}
// end::pattern[]

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
        <input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/>
        <table>
          <tbody>
            <tr>
              <th>Number</th>
              <th>Program</th>
            </tr>
            {channels}
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

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]