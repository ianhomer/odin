/**
 * Create a new element in the global document in which test components can be attached to.  This
 * is useful when any code being tested makes use of the global document object.
 */
function newDocumentElement() {
  const div = document.createElement('div')
  document.body.appendChild(div)
  return div
}

module.exports = {newDocumentElement}