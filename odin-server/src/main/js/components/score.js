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

function concat(a, b) {
  return a.concat(b);
}

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
      this.renderNotation(event.target.value, true);
      this.setState( {notation: event.target.value, count: this.state.count + 1} );
    } catch (error) {
      console.warn('Cannot draw score so not updating state : ' + error.message);
    }
  }

  getElementId(extra = '') {
    // Element ID changes are each handleChange call.  Since the key attribute of the score element
    // set to this changing value, React will force a reload of the element, otherwise React is
    // not aware that the element is changing (via the VexFlow API) and so VexFlow draw keeps
    // appending further scores instead of replacing.
    var ending = '-notation-' + this.state.count;
    if (extra != '') {
      ending += '-' + extra;
    }
    if (this.props.entity && this.props.entity._links) {
      return this.props.entity._links.self.href + ending;
    } else {
      return this.props.elementKey + ending;
    }
  }

  renderNotation(notation = this.state.notation, dryRun = false) {
    // JSON Object for composition
    var composition = {
      measures : [
        {
          key : 'C',
          time : '4/4',
          staves : [
            {
              clef : 'treble',
              notes : notation
            }
          ]
        }
      ]
    };

    var selector;
    if (dryRun) {
      // For dry run we render score in a hidden element so that we that end user does not
      // see a blank canvas on error.
      selector = this.getElementId('hidden');
    } else {
      selector = this.getElementId();
    }

    var vf = new Vex.Flow.Factory({
      renderer: {selector: selector, width: 500, height: 100}
    });

    vf.reset();
    vf.getContext().clear();

    var score = vf.EasyScore();

    var voice = score.voice.bind(score);
    var notes = score.notes.bind(score);

    var x = 0;
    var y = 0;

    // Render composition
    for (var i = 0 ; i < composition.measures.length ; i++) {

      // Render measure
      var measure = composition.measures[i];

      var width = 200;
      var system = vf.System({ x: x, y: y, width: width, spaceBetweenStaves: 10 });
      x += width;

      // Render stave
      for (var j = 0 ; j < measure.staves.length ; j++) {
        var stave = measure.staves[j];
        system.addStave({
          voices: [
            voice([
              notes(stave.notes)
            ].reduce(concat))
          ]
        })
          .addClef(stave.clef)
          .addKeySignature(measure.key)
          .addTimeSignature(measure.time);
      }
    }

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
          <div id={this.getElementId('hidden')} className="hidden"/>
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