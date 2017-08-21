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

const client = require('../client');
const VALIDATE_FIRST = false;

function concat(a, b) {
  return a.concat(b);
}

// Musical score component.
class Score extends React.Component{
  constructor(props) {
    super(props);

    this.state = {
      notation: this.props.entity.notation,
      count: 1,
      // Store dimensions of score element as state.  Note that at some point the server
      // may suggest alternative dimensions and when it does this style in the state should
      // be updated.
      style: {height : 100, width : 200}
    };

    this.handleChange = this.handleChange.bind(this);
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
      if (VALIDATE_FIRST) {
        this.renderNotation(event.target.value, true);
      }
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
    // Resolve composition structure from this notation
    client({
      method: 'GET',
      path: '/services/composition',
      params: {'notation' : notation},
      headers: {'Accept': 'application/json'}
    }).then(response => {
      this.renderComposition(response.entity, dryRun);
    }).catch(reason => {
      console.error(reason);
    });
  }

  // Remove previous score canvas that might have been drawn
  removePreviousCanvases(elementId) {
    var element = document.getElementById(elementId);
    if (element) {
      var canvases = element.getElementsByTagName('svg');
      for (var i = 0; i < canvases.length ; i++) {
        element.removeChild(canvases[i]);
      }
    }
  }

  renderComposition(composition, dryRun = false) {
    var selector;
    if (dryRun) {
      // For dry run we render score in a hidden element so that we that end user does not
      // see a blank canvas on error.
      selector = this.getElementId('hidden');
    } else {
      selector = this.getElementId();
    }

    this.removePreviousCanvases(selector);

    var element = document.getElementById(selector);
    if (!element) {
      console.warn('Cannot find selector ' + selector);
      return;
    }
    var vf = new Vex.Flow.Factory({
      renderer: {selector: selector, width: this.state.style.width, height: this.state.style.height}
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
        var staff = measure.staves[j];

        // TODO : Support more than one voice per staff
        var systemStaff = system.addStave({
          voices: [
            voice([
              notes(staff.voices[0].notation)
            ].reduce(concat))
          ]
        });

        if (i == 0 || composition.measures[i-1].staves[j].clef != staff.clef) {
          systemStaff.addClef(staff.clef);
        }

        if (i == 0 || composition.measures[i-1].key != measure.key) {
          systemStaff.addKeySignature(measure.key);
        }

        if (i == 0 || composition.measures[i-1].time != measure.time) {
          systemStaff.addTimeSignature(measure.time);
        }
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
              ref={this.props.componentRef} className="inline"
              defaultValue={this.state.notation}
              onKeyPress={this.props.onKeyPress}
              onChange={this.handleChange}
              size={this.props.size}
            />
          </span>
          <div key={this.getElementId()} id={this.getElementId()} style={this.state.style}/>
          {VALIDATE_FIRST &&
            <div id={this.getElementId('hidden')} className="hidden"/>
          }
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