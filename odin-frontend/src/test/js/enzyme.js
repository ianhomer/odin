const Adapter = require('enzyme-adapter-react-16')
const enzyme = require('enzyme')
enzyme.configure({adapter: new Adapter()})
const {mount, shallow} = require('enzyme')
module.exports = {mount, shallow}