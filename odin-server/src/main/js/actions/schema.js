export const LOAD_PROJECT_SCHEMA_REQUESTED = 'LOAD_PROJECT_SCHEMA_REQUESTED'
export function loadProjectSchemaRequested() {
  return {
    type: LOAD_PROJECT_SCHEMA_REQUESTED
  }
}

export const LOAD_PROJECT_SCHEMA_SUCCEEDED = 'LOAD_PROJECT_SCHEMA_SUCCEEDED'
export const LOAD_PROJECT_SCHEMA_FAILED = 'LOAD_PROJECT_SCHEMA_FAILED'

export const LOAD_PROFILE_SCHEMA_REQUESTED = 'LOAD_PROFILE_SCHEMA_REQUESTED'
export function loadProfileSchemaRequested(path) {
  return {
    type: LOAD_PROFILE_SCHEMA_REQUESTED,
    path
  }
}

export const LOAD_PROFILE_SCHEMA_SUCCEEDED = 'LOAD_PROFILE_SCHEMA_SUCCEEDED'
export const LOAD_PROFILE_SCHEMA_FAILED = 'LOAD_PROFILE_SCHEMA_FAILED'

export const loadSchemaActions = [
  loadProjectSchemaRequested(),
  loadProfileSchemaRequested('sequence'),
  loadProfileSchemaRequested('channel')
]
