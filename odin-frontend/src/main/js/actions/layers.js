import {patchEntityRequested} from './entities'

function getAddLayerToEntityOperations(entity, layer) {
  return [
    {op: 'add', path: '/layers/-', value: layer.name}
  ]
}

function getRemoveLayerOperations(entity, layer) {
  // TODO : destination is null when when layer comes from a root layer.  Checking for null
  // like this is NOT robust since there might be other reasons for null that we end up
  // swallowing.  We should address this shortcoming.
  if (entity != null) {
    var layerIndex = entity.layers.indexOf(layer.name)
    if (layerIndex > -1) {
      var layerPath = '/layers/' + layerIndex
      return [
        // Test layer at given index in array is the as expected on server
        {op: 'test', path: layerPath, value: layer.name},
        // ... then remove it.
        {op: 'remove', path: layerPath}
      ]
    } else {
      console.warn('Cannot find ' + JSON.stringify(layer) + ' in '
        + JSON.stringify(entity) + ' to remove it')
    }
  } else {
    return []
  }
}

export function addLayerToEntityRequested(entity, layer) {
  return patchEntityRequested(entity, getAddLayerToEntityOperations(entity, layer))
}

export function removeLayerRequested(entity, layer, nextAction) {
  return patchEntityRequested(entity, getRemoveLayerOperations(entity, layer), nextAction)
}

export function moveLayerRequested(destination, from, layer) {
  if (from) {
    return removeLayerRequested(from, layer,
      addLayerToEntityRequested(destination, layer)
    )
  } else {
    // TODO : Confirm that this flow is OK, not sure if from should ever be null
    // Moving layer from root level (i.e. no from set)
    return addLayerToEntityRequested(destination, layer)
  }
}