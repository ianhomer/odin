export const LOAD_PERFORMANCE_SCHEMA_REQUESTED = 'LOAD_PERFORMANCE_SCHEMA_REQUESTED'
export function loadPerformanceSchemaRequested() {
  return {
    type: LOAD_PERFORMANCE_SCHEMA_REQUESTED
  }
}

export const LOAD_PERFORMANCE_SCHEMA_SUCCEEDED = 'LOAD_PERFORMANCE_SCHEMA_SUCCEEDED'
export const LOAD_PERFORMANCE_SCHEMA_FAILED = 'LOAD_PERFORMANCE_SCHEMA_FAILED'

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
  loadPerformanceSchemaRequested(),
  loadProfileSchemaRequested('sequence'),
  loadProfileSchemaRequested('channel')
]
