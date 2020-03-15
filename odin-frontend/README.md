# Odin Front End

Quick install development version of front end

    yarn test

## Hot Redeployment and Development cycle

   See Server Development with Hot Deployment in root DEVELOPER.md

## Running JS tests

Run JS tests

    yarn test

Run a single JS test

    yarn test Clazz.test.js

## Run JS test with console log output

    jest --silent=false Clazz.test.js

## Debug JS

To debug a single JS test, add the line

    debugger

into the script to define a break point then start test with

    ./jest-debug.sh Clazz.test.js

Open up chrome://inspect in Chrome, click inspect and play icon and step through
the debugger

## Update Snapshots

  jest --updateSnapshot

or

  jest -u

## Docker pod recycle

    mvn install -P quick                    && \
      docker run --name odin_odin-frontend_1

