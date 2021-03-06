// Copyright (c) 2017 The Odin Authors. All Rights Reserved
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

import {LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PERFORMANCE_SCHEMA_SUCCEEDED} from '../actions'

function profiles(state = {}, action) {
  switch (action.type) {
  case LOAD_PROFILE_SCHEMA_SUCCEEDED:
    return {...state,
      [action.path]: action.schema
    }
  default:
    return state
  }
}

function schema(state = {revision: 0}, action) {
  switch (action.type) {
  case LOAD_PERFORMANCE_SCHEMA_SUCCEEDED:
    return {...state,
      revision: state.revision + 1,
      performance: action.schema
    }
  case LOAD_PROFILE_SCHEMA_SUCCEEDED:
    return {...state,
      revision: state.revision + 1,
      profiles: profiles(state.profiles, action)
    }
  default:
    return state
  }
}

export default schema