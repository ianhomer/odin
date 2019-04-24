export const ItemTypes = {
  LAYER: 'layer',
  SEQUENCE: 'sequence',
  SEQUENCE_IN_LAYER: 'sequenceInLayer'
}

// If global variable odinApiHost is set then use that as the host, otherwise we expect it's local
/* global odinApiHost */
export const apiRoot = (typeof odinApiHost !== 'undefined' ? odinApiHost : '') + '/api'