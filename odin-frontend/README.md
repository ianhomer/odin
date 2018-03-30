Quick install development version of front end

    mvn frontend:webpack

## Running JS tests

Run JS tests

    npm test
    
Run a single JS test

    npm test -- channelsContainer.test.js

## Run JS test with console log output

    jest --silent=false environmentContainer.test.js

## Debug JS

To debug a single JS test, add the line

    debugger 
    
into the script to define a break point then start test with    
    
    ./jest-debug.sh clazz.test.js
    
Open up chrome://inspect in Chrome, click inspect and play icon and step through the debugger

## Update Snapshots

  jest --updateSnapshot

or
  
  jest -u  