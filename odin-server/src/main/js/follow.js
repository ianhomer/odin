// With thanks to https://spring.io/guides/tutorials/react-and-spring-data-rest/
//
// The first argument to the follow() function is the client object used to make REST calls.
// The second argument is the root URI to start from.
// The third argument is an array of relationships to navigate along. Each one can be a string or an object.
//
module.exports = function follow(api, rootPath, relArray) {
  var root = api({
    method: 'GET',
    path: rootPath
  });


  return relArray.reduce(function(root, arrayItem) {
    var rel = typeof arrayItem === 'string' ? arrayItem : arrayItem.rel;
    return traverseNext(root, rel, arrayItem);
  }, root);

  function traverseNext (root, rel, arrayItem) {
    return root.then(function (response) {
      if (hasEmbeddedRel(response.entity, rel)) {
        return response.entity._embedded[rel];
      }

      if(!response.entity._links) {
        return [];
      }
      if(!response.entity._links[rel]) {
        console.warn(root.path + ' _links[' + rel + '] is not defined');
        return [];
      }

      if (typeof arrayItem === 'string') {
        return api({
          method: 'GET',
          path: response.entity._links[rel].href
        });
      } else {
        return api({
          method: 'GET',
          path: response.entity._links[rel].href,
          params: arrayItem.params
        });
      }
    });
  }

  function hasEmbeddedRel (entity, rel) {
    return entity._embedded && entity._embedded.hasOwnProperty(rel);
  }
};