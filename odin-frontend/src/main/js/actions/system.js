export const FETCH_SYSTEM_REQUESTED = 'FETCH_SYSTEM_REQUESTED'
export const FETCH_SYSTEM_SUCCEEDED = 'FETCH_SYSTEM_SUCCEEDED'
export const FETCH_SYSTEM_FAILED = 'FETCH_SYSTEM_FAILED'

export function fetchSystemRequested(path) {
  return {
    type: FETCH_SYSTEM_REQUESTED,
    path
  }
}