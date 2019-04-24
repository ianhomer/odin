export const withQuery = function(uri, params) {
  return uri + (uri.indexOf('?') === -1 ? '?' : '&') +
    Object.keys(params)
      .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(params[key]))
      .join('&')
}

export const secure = function(headers) {
  var csrfToken = document.head.querySelector('meta[name="_csrf"]')
  if (csrfToken) {
    return {...headers,
      'X-CSRF-TOKEN' : csrfToken.getAttribute('content')
    }
  }
  return headers
}

export const json = function() {
  return {'Content-Type': 'application/json'}
}

export const jsonPatch = function() {
  return {'Content-Type': 'application/json-patch+json'}
}


