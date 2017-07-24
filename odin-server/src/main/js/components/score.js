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
    this.state = {notation: this.props.entity.notation};
  }

  componentDidMount() {
    this.renderNotation();
  }

  componentDidUpdate() {
    this.renderNotation();
  }

  handleChange(event) {
    console.log('Change : ' + event.target.value);
    this.setState( {notation: event.target.value} );
  }

  getElementId() {
    if (this.props.entity && this.props.entity._links) {
      return this.props.entity._links.self.href + '-notation';
    } else {
      return this.props.elementKey + '-notation';
    }
  }

  renderNotation() {

    var vf = new Vex.Flow.Factory({
      renderer: {selector: this.getElementId(), width: 500, height: 200}
    });

    var score = vf.EasyScore();
    var system = vf.System();

    console.log('Rendering score : ' + this.state.notation);
    system.addStave({
      voices: [
        score.voice(score.notes(this.state.notation))
      ]
    }).addClef('treble').addTimeSignature('4/4');

    vf.draw();
  }

  render() {
    console.log('Rendering score');
    var elementId;

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
          <span id={this.getElementId()}/>
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