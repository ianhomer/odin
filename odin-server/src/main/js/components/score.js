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
  }

  componentDidMount() {
    this.renderNotation();
  }

  renderNotation() {

    var vf = new Vex.Flow.Factory({
      renderer: {selector: this.props.sequence._links.self.href + '-notation', width: 500, height: 200}
    });

    var score = vf.EasyScore();
    var system = vf.System();

    system.addStave({
      voices: [
        score.voice(score.notes('C#5/q, B4, A4, G#4', {stem: 'up'})),
        score.voice(score.notes('C#4/h, C#4', {stem: 'down'}))
      ]
    }).addClef('treble').addTimeSignature('4/4');

    vf.draw();
  }

  render() {

    return (
      <div className="component row">
        Score : {this.props.sequence.notation}
        <div id={this.props.sequence._links.self.href + '-notation'}/>
      </div>
    );
  }
}

module.exports = Score;