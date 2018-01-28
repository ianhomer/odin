var path = require('path');

module.exports = {
  entry: './src/main/js/index.js',
  cache: true,
  output: {
    path: __dirname,
    filename: './src/main/resources/static/built/bundle.js'
  },
  module: {
    loaders: [
      {
        test: path.join(__dirname, '.'),
        exclude: /(node_modules)/,
        loader: 'babel-loader',
        query: {
          cacheDirectory: true,
          presets: ['es2015', 'react']
        }
      },
      { test: /\.json$/, loader: "json-loader" },
      { test: /\.coffee/, loader: "coffee-loader" }
    ]
  }
};