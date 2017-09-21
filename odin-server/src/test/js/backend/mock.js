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

// Mock Backend

'use strict'

import testChannels from '../data/channel.json'
import testSchema from '../data/schema.json'
import { Backend } from 'odin/backend/index.js'

export class MockBackend extends Backend {
  createEntityApi(entity) {
    return entity
  }

  deleteEntityApi(entity) {
    return entity
  }

  // Load entities from JSON imported from file
  loadEntitiesApi(path) {
    var json = ((path) => {
      switch (path) {
      case 'channel':
        return testChannels
      default:
        throw new Error('No test data available for path ' + path)
      }
    })(path)
    // TODO : Load schema for path

    return json._embedded.channel
  }

  loadSchemaApi() {
    return testSchema
  }
}