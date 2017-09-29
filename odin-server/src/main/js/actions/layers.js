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
  patchEntityRequested(entity, getAddLayerToEntityOperations(entity, layer))
}

export function removeLayerRequested(entity, layer) {
  var operations = getRemoveLayerOperations(entity, layer)
  if (operations.length > 0) {
    patchEntityRequested(entity, operations)
  }
}

export function moveLayerRequested(destination, from, layer) {
  removeLayerRequested(from, layer)
  addLayerToEntityRequested(destination, layer)
}