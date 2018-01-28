const merge = require('webpack-merge');
const common = require('./webpack.common.js');

// This configuration file creates a development built.  It is used when the front end
// is built with mvn frontend:webpack
module.exports = merge(common, {
  devtool: 'sourcemaps'
});