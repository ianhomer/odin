import { connect } from 'react-redux'
import { createEntityRequested, deleteEntityRequested, loadEntitiesRequested } from '../actions'

// Curry mapStateToProps with path value
function mapStateToProps(path) {
  return function(state) {
    const entities = state.entities[path] ? state.entities[path].entities : []

    return {
      entities
    }
  }
}

function mapDispatchToProps(dispatch) {
  return {
    onCreate : (entity, path) => {
      dispatch(createEntityRequested(entity, path))
    },
    onDelete: entity => {
      dispatch(deleteEntityRequested(entity))
    },
    onLoadEntities : (path, schema) => {
      dispatch(loadEntitiesRequested(path, schema))
    }
  }
}


export default function connectEntities(path, component) {
  return connect(mapStateToProps(path), mapDispatchToProps)(component)
}