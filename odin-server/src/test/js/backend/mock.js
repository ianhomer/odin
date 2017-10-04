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

import testSchema from '../data/schema.json'
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
      '.  Tried loading from '+ fullPath)
  }
}

var counter = 0
// TODO : Make this ID allocation threadsafe
const createId = function() {
  console.log(counter)
  return counter++
}

const safe = function(entity, path) {
  if (!entity.properties) {
    entity.properties = {}
  }
  if (!entity._links) {
    entity._links = {self: {href: 'http://localhost:8080/api/' + path + '/' + createId()}}
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

  // Load entities from JSON imported from file
  loadEntitiesApi(path) {
    var json = (path => {
      try {
        return loadTestData('api', path)
      } catch (e) {
        console.error(e)
        throw new Error('No test entities data available for path ' + path)
      }
    })(path)
    return json._embedded[path]
  }

  loadProjectSchemaApi() {
    return testSchema
  }

  loadProfileSchemaApi(path) {
    return loadTestData('api/profile', path)
  }

  fetchCompositionApi(notation) {
    return loadTestData('compositions', notation)
  }
}