const path = require('path');

module.exports = {
  mode: 'development',
  entry: './src/index.js',
  cache: true,
  output: {
    path: __dirname,
    filename: './src/main/resources/static/built/bundle.js'
  },

  module: {
    rules: [
      {
        test: path.join(__dirname, '.'),
        exclude: /(node_modules)/,
        loader: 'babel-loader',
        query: {
          cacheDirectory: true,
          presets: ['es2015', 'react']
        }
      },
      { test: /\.coffee/, loader: 'coffee-loader' }
    ]
  }
};