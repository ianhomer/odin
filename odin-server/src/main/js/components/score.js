// Copyright (c) 2017 the original author or authors. All Rights Reserved
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

'use strict'

const React = require('react')
const PropTypes = require('prop-types')
const Vex = require('vexflow')

function concat(a, b) {
  return a.concat(b)
}

// Musical score component.
class Score extends React.Component{
  constructor(props) {
    super(props)

    this.state = {
      count: 1,
      // Store dimensions of score element as state.  Note that at some point the server
      // may suggest alternative dimensions and when it does this style in the state should
      // be updated.
      style: {height : this.props.height + 'px', width : this.props.width + 'px'}
    }

    this.handleChange = this.handleChange.bind(this)
  }

  componentDidMount() {
    this.componentDidUpdate()
  }

  componentDidUpdate() {
    try {
      this.renderNotation()
    } catch (error) {
      console.error(error, 'Cannot draw score')
    }
  }

  handleChange(event) {
    try {
      this.setState({notation: event.target.value, count: this.state.count + 1})
      this.props.onFetchComposition(this.props.entity.name, event.target.value)
    } catch (error) {
      console.warn('Cannot draw score so not updating state : ' + error.message)
    }
  }

  getElementId(extra = '') {
    // Element ID changes are each handleChange call.  Since the key attribute of the score element
    // set to this changing value, React will force a reload of the element, otherwise React is
    // not aware that the element is changing (via the VexFlow API) and so VexFlow draw keeps
    // appending further scores instead of replacing.
    var ending = '-notation-' + this.state.count
    if (extra != '') {
      ending += '-' + extra
    }
    if (this.props.entity && this.props.entity._links) {
      return this.props.entity._links.self.href + ending
    } else {
      return this.props.elementKey + ending
    }
  }

  getNotation() {
    if (this.state.notation) {
      // Return changed but unsaved notation if it has been set
      return this.state.notation
    }
    // Otherwise the value provided by component's parent
    return this.props.entity.properties && this.props.entity.properties.notation
  }

  isNotationDirty() {
    return !!this.state.notation
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.entity.properties && nextProps.entity.properties.notation === this.state.notation) {
      // If notation has been persisted then we can clear the state setting
      this.setState({notation:null})
    }
  }

  renderNotation() {
    // Use composition in store
    if (this.props.entity._composition) {
      this.renderComposition(this.props.entity._composition)
    }
  }

  // Remove previous score canvas that might have been drawn
  removePreviousCanvases(elementId) {
    var element = document.getElementById(elementId)
    if (element) {
      var canvases = element.getElementsByTagName('svg')
      for (var i = 0; i < canvases.length ; i++) {
        element.removeChild(canvases[i])
      }
    }
  }

  renderComposition(composition) {
    var selector = this.getElementId()

    this.removePreviousCanvases(selector)

    var element = document.getElementById(selector)
    if (!element) {
      console.warn('Cannot find selector ' + selector)
      return
    }
    var vf = new Vex.Flow.Factory({
      renderer: {elementId: selector, width: this.props.width, height: this.props.height}
    })

    vf.reset()
    vf.getContext().clear()

    var score = vf.EasyScore()

    var voice = score.voice.bind(score)
    var notes = score.notes.bind(score)

    var x = 0
    var y = 0

    // Render composition
    for (var i = 0 ; i < composition.measures.length ; i++) {

      // Render measure
      var measure = composition.measures[i]

      var width = 200
      var system = vf.System({ x: x, y: y, width: width, spaceBetweenStaves: 10 })
      x += width

      // Render stave
      for (var j = 0 ; j < measure.staves.length ; j++) {
        var staff = measure.staves[j]

        // TODO : Support more than one voice per staff
        var systemStaff = system.addStave({
          voices: [
            voice([
              notes(staff.voices[0].notation)
            ].reduce(concat))
          ]
        })

        if (i == 0 || composition.measures[i-1].staves[j].clef != staff.clef) {
          systemStaff.addClef(staff.clef)
        }

        if (i == 0 || composition.measures[i-1].key != measure.key) {
          systemStaff.addKeySignature(measure.key)
        }

        if (i == 0 || composition.measures[i-1].time != measure.time) {
          systemStaff.addTimeSignature(measure.time)
        }
      }
    }

    vf.draw()
  }

  render() {
    if (this.props.editor) {
      return (
        <div>
          <span>
            <input type="text" placeholder={this.props.componentKey}
              ref={this.props.componentRef} className="inline"
              defaultValue={this.getNotation()}
              onKeyPress={this.props.onKeyPress}
              onChange={this.handleChange}
              size={this.props.size}
            />
          </span>
          <div key={this.getElementId()} id={this.getElementId()} style={this.state.style}/>
        </div>
      )
    } else {
      return (
        <div>
          {this.props.displayText &&
            <span>{this.getNotation()}</span>
          }
          <span id={this.getElementId()}/>
        </div>
      )
    }
  }
}

Score.defaultProps = {
  height: 120,
  width: 200,
}

Score.propTypes = {
  entity: PropTypes.shape({
    name: PropTypes.string.isRequired,
    properties: PropTypes.shape({
      notation: PropTypes.string.isRequired
    }),
    _composition: PropTypes.object,
    _links: PropTypes.shape({
      self: PropTypes.shape({
        href: PropTypes.string.isRequired
      })
    })
  }),
  displayText: PropTypes.bool,
  editor: PropTypes.bool,
  height: PropTypes.number.isRequired,
  flux: PropTypes.object.isRequired,
  size: PropTypes.number,
  width: PropTypes.number.isRequired,
  // TODO : Confirm following necessary
  componentKey: PropTypes.string,
  componentRef: PropTypes.func,
  elementKey: PropTypes.string,
  onFetchComposition: PropTypes.func.isRequired,
  onKeyPress: PropTypes.func
}

module.exports = Score