import { connect } from 'react-redux'
import { createEntityRequested, deleteEntityRequested,
  updateEntityRequested, loadEntitiesRequested } from '../actions'

// Curry mapStateToProps with path value
function mapStateToProps(path) {
  return function(state) {
    const collection = state.collections[path] ? state.collections[path] : { entities : [] }

    return {
      collection
    }
  }
}

export function mapDispatchToProps(dispatch) {
  return {
    onCreate : (entity, path) => {
      dispatch(createEntityRequested(entity, path))
    },
    onDelete: entity => {
      dispatch(deleteEntityRequested(entity))
    },
    onUpdate: entity => {
      dispatch(updateEntityRequested(entity))
    },
    onLoadEntities : (path, schema) => {
      dispatch(loadEntitiesRequested(path, schema))
    }
  }
}


export function connectEntities(path, component, _mapDispatchToProps = mapDispatchToProps) {
  return connect(mapStateToProps(path), _mapDispatchToProps)(component)
}