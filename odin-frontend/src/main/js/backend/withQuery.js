export const withQuery = function(uri, params) {
  return uri + (uri.indexOf('?') === -1 ? '?' : '&') +
    Object.keys(params)
      .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(params[key]))
      .join('&')
}