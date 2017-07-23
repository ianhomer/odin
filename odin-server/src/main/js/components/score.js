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
    var VF = Vex.Flow;

    // Create an SVG renderer and attach it to the DIV element named "boo".
    var div = document.getElementById('boo');
    var renderer = new VF.Renderer(div, VF.Renderer.Backends.SVG);

    // Configure the rendering context.
    renderer.resize(500, 200);
    var context = renderer.getContext();
    context.setFont('Arial', 10, '').setBackgroundFillStyle('#eed');

    // Create a stave of width 400 at position 10, 40 on the canvas.
    var stave = new VF.Stave(10, 40, 400);

    // Add a clef and time signature.
    stave.addClef('treble').addTimeSignature('4/4');

    // Connect it to the rendering context and draw!
    stave.setContext(context).draw();
  }

  render() {

    return (
      <div className="component row">
        Score : {this.props.notation}
        <div id="boo"/>
      </div>
    );
  }
}

module.exports = Score;