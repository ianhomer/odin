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

import testSchema from '../data/api/services/schema.json'
import {Backend} from 'odin/backend/index.js'
import fs from 'fs'

const loadTestData = function(root, path) {
  var fullPath = __dirname + '/../data/' + (root ? root + '/' : '') + encodeURIComponent(path) + '.json'
  if (!fs.existsSync(fullPath)) {
    const defaultPath = __dirname + '/../data/' + root + '/default.json'
    if (fs.existsSync(defaultPath)) {
      fullPath = defaultPath
    }
  }

  try {
    return JSON.parse(fs.readFileSync(fullPath))
  } catch (e) {
    console.error(e)
    throw new Error('No test profile data available for path ' + path +
      '.  Also tried loading from '+ fullPath)
  }
}

var counter = 0
// TODO : Make this ID allocation threadsafe
const createId = function() {
  return counter++
}

const safe = function(entity, path) {
  if (!entity.properties) {
    entity.properties = {}
  }
  if (!entity._links) {
    entity._links = {self: {href: 'http://localhost:8080/api/rest/' + path + '/' + createId()}}
  }
  if (path === 'layer') {
    if (!entity.layers) {
      entity.layers = []
    }
  }
  return entity
}

export class MockBackend extends Backend {
  createEntityApi(entity, path) {
    return safe(entity, path)
  }

  updateEntityApi(entity) {
    return safe(entity)
  }

  deleteEntityApi(entity) {
    return entity
  }

  patchEntityApi(entity, patch) {
    for (var i=0 ; i< patch.length ; i++) {
      var [, propertyName, index] = patch[i].path.match('/([^/]*)/([0-9]*)')
      switch (patch[i].op) {
      case 'remove':
        entity[propertyName].splice(index, 1)
        break
      case 'add':
        if (!entity[propertyName]) {
          entity[propertyName] = []
        }
        entity[propertyName].push(patch[i].value)
        break
      case 'test':
        if (entity[propertyName][index] !== patch[i].value) {
          throw new Error(propertyName + ' index ' + index + ' is not ' + patch[i].value)
        }
        break
      default:
        throw new Error('Operation ' + patch[i].op + ' not supported in mock backend')
      }
    }
    return entity
  }

  // Load entities from JSON imported from file
  loadEntitiesApi(path) {
    var json = (path => {
      try {
        return loadTestData('api/rest', path)
      } catch (e) {
        console.error(e)
        throw new Error('No test entities data available for path ' + path)
      }
    })(path)
    return json._embedded[path]
  }

  loadPerformanceSchemaApi() {
    return testSchema
  }

  loadProfileSchemaApi(path) {
    return loadTestData('api/rest/profile', path)
  }

  fetchCompositionApi(notation) {
    return loadTestData('api/services/composition', notation)
  }

  fetchSystemApi(path) {
    return loadTestData('api/services/system/', path)
  }
}