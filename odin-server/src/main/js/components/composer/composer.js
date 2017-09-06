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
import { DragDropContext } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
const PropTypes = require('prop-types');

const crud = require('../../crud');

const LayerList = require('./layerList');
const NotationCard = require('./notationCard');
const PatternCard = require('./patternCard');
const SequenceCard = require('./sequenceCard');
const Sequences = {
  'notation' : NotationCard,
  'pattern'  : PatternCard,
};
const DefaultSequence = SequenceCard;

// Rendering of composer
class Composer extends React.Component{
  constructor(props) {
    super(props);
    // TODO : Remove schema and links if redundant
    this.state = {
      schema: [], entities: [], links: []
    };

    crud.bindMe(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleAddLayer = this.handleAddLayer.bind(this);
    this.handleMoveLayer = this.handleMoveLayer.bind(this);
    this.onPatch = crud.onPatch.bind(this);
  }

  componentDidMount() {
    this.context.schema.loadSchemas(['notation', 'pattern']).then(() => {
      this.loadFromServer();
    });
  }

  handleChange() {
    // TODO : Look into Redux for state management, currently we're reloading the whole
    // view each time.
    this.loadFromServer();
  }

  handleAddLayer(destination, layer, callback = this.handleChange) {
    this.onPatch(destination._links.self.href,
      [
        { op: 'add', path: '/layers/-', value: layer.name }
      ], callback);
  }

  handleRemoveLayer(destination, layer, callback = this.handleChange) {
    // TODO : destination is null when when layer comes from a root layer.  Checking for null
    // like this is NOT robust since there might be other reasons for null that we end up
    // swallowing.  We should address this shortcoming.
    if (destination != null) {
      var layerIndex = destination.layers.indexOf(layer.name);
      if (layerIndex > -1) {
        var layerPath = '/layers/' + layerIndex;
        this.onPatch(destination._links.self.href,
          [
            // Test layer at given index in array is the as expected on server
            { op: 'test', path: layerPath, value: layer.name },
            // ... then remove it.
            { op: 'remove', path: layerPath  }
          ], callback);
      } else {
        console.warn('Cannot find ' + JSON.stringify(layer) + ' in '
          + JSON.stringify(destination) + ' to remove it');
      }
    } else {
      callback();
    }
  }

  handleMoveLayer(destination, from, layer, callback = this.handleChange) {
    this.handleRemoveLayer(from, layer, () => {
      this.handleAddLayer(destination, layer, callback);
    });
  }

  render() {
    var entities = this.state.entities.map(entity => {
      var SequenceComponent = Sequences[entity.flowName] || DefaultSequence;
      if (!entity.path) {
        console.error('Entity path not defined for ' + entity);
        return (<div/>);
      } else {
        return (
          <div key={'div-' + entity._links.self.href}>
            <SequenceComponent entity={entity} key={entity._links.self.href}
              project={this.props.project}
              path={entity.path}
              onAddLayer={this.handleAddLayer}
            />
          </div>
        );
      }
    });

    return (
      // Display layer list and sequence cards
      <div>
        {this.state.entities.length > 0 &&
          <LayerList project={this.props.project} sequences={this.state.entities}
            onChange={this.handleChange}
            onAddLayer={this.handleAddLayer}
            onMoveLayer={this.handleMoveLayer}
          />
        }
        <div className="break">&nbsp;</div>
        <div>{entities}</div>
      </div>
    );
  }
}

Composer.defaultProps = {
  path: 'sequence'
};

Composer.contextTypes = {
  schema: PropTypes.object
};

module.exports = DragDropContext(HTML5Backend)(Composer);