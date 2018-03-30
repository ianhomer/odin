export const FETCH_COMPOSITION_REQUESTED = 'FETCH_COMPOSITION_REQUESTED'
export const FETCH_COMPOSITION_SUCCEEDED = 'FETCH_COMPOSITION_SUCCEEDED'
export const FETCH_COMPOSITION_FAILED = 'FETCH_COMPOSITION_FAILED'

export function fetchCompositionRequested(entityName, notation) {
  return {
    type: FETCH_COMPOSITION_REQUESTED,
    entityName,
    notation
  }
}