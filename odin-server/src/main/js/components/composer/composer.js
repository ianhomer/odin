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

const crud = require('../../crud');

const LayerList = require('./layerList');
const Trash = require('./trash');
const NotationCard = require('./notationCard');
const PatternCard = require('./patternCard');
const Sequences = {
  'notations' : NotationCard,
  'patterns'  : PatternCard
};

// Rendering of composer
class Composer extends React.Component{
  constructor(props) {
    super(props);
    this.state = {
      schema: [], entities: [], links: []
    };

    crud.bindMe(this);
  }

  componentDidMount() {
    crud.loadSchemas(['notations', 'patterns']).then(() => {
      this.loadFromServer();
    });
  }

  render() {
    var entities = this.state.entities.map(entity => {
      var SequenceCard = Sequences[entity.path];
      if (!entity.path) {
        console.error('Entity path not defined for ' + entity);
        return (<div/>);
      } else {
        return (
          <div key={'div-' + entity._links.self.href}>
            <SequenceCard entity={entity} key={entity._links.self.href}
              project={this.props.project}
              path={entity.path}
            />
          </div>
        );
      }
    });

    return (
      // Display layer list and sequence cards
      <div>
        <LayerList project={this.state.project} sequences={this.state.entities}/>
        <Trash/>
        <div className="break">&nbsp;</div>
        <div>{entities}</div>
      </div>
    );
  }
}

Composer.defaultProps = {
  path: 'sequences'
};

module.exports = DragDropContext(HTML5Backend)(Composer);