// Copyright (c) 2017 Ian Homer. All Rights Reserved
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

'use strict';

const React = require('react');
const Vex = require('vexflow');

// Musical score component.
class Score extends React.Component{
  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
    this.state = {notation: this.props.entity.notation, count: 1};
  }

  componentDidMount() {
    this.componentDidUpdate();
  }

  componentDidUpdate() {
    try {
      this.renderNotation();
    } catch (error) {
      console.error(error, 'Cannot draw score');
    }
  }

  handleChange(event) {
    try {
      this.renderNotation(event.target.value);
      this.setState( {notation: event.target.value, count: this.state.count + 1} );
    } catch (error) {
      console.warn('Cannot draw score so not updating state : ' + error.message);
    }
  }

  getElementId() {
    // Element ID changes are each handleChange call.  Since the key attribute of the score element
    // set to this changing value, React will force a reload of the element, otherwise React is
    // not aware that the element is changing (via the VexFlow API) and so VexFlow draw keeps
    // appending further scores instead of replacing.
    var ending = '-notation-' + this.state.count;
    if (this.props.entity && this.props.entity._links) {
      return this.props.entity._links.self.href + ending;
    } else {
      return this.props.elementKey + ending;
    }
  }

  renderNotation(notation = this.state.notation) {
    var vf = new Vex.Flow.Factory({
      renderer: {selector: this.getElementId(), width: 500, height: 200}
    });

    vf.reset();
    vf.getContext().clear();
    var score = vf.EasyScore();
    var system = vf.System();

    system.addStave({
      voices: [
        score.voice(score.notes(notation))
      ]
    }).addClef('treble').addTimeSignature('4/4');

    vf.draw();
  }

  render() {
    if (this.props.editor) {
      return (
        <div>
          <span>
            <input type="text" placeholder={this.props.componentKey}
              ref={this.props.componentRef} className="field"
              defaultValue={this.state.notation}
              onKeyPress={this.props.onKeyPress}
              onChange={this.handleChange}
              size={this.props.size}
            />
          </span>
          <div key={this.getElementId()} id={this.getElementId()}/>
        </div>
      );
    } else {
      return (
        <div>
          {this.props.displayText &&
            <span>{this.state.notation}</span>
          }
          <span id={this.getElementId()}/>
        </div>
      );
    }
  }
}

module.exports = Score;