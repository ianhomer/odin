# odin

The Odin sequencer enhances a live musical performance by musicians.  This
includes the following use cases :

* Composition support by trying out themes, progressions and riffs to help settle on a performance.
* Jamming support by delivering common musical sequences that a band can jam around and practice.
* Delivery samples at appropriate time during a performance.
* Adjusting effects during a performance.

Performances can be written in a DSL that describes the semantics and specifics of a performance
describing chord progressions and musical hints, along with specific sound delivery.

The system will also support experiments involving how machines can enhance live musical 
performances and how musicians can act as conductor to the machine, perhaps letting go of the 
reigns entirely.  

Over time the system will drive other aspects of a performance beyond sound and will take direction
from machine learning. 

# Release Notes

The first release of this system will focus on driving live music performance via MIDI and 
will be installable on a Raspberry Pi to encourage portability and reduced costs.

# Build

    mvn package
    java -jar odin-server/target/odin-server-1.0-SNAPSHOT.jar

or run with developer benefits, e.g. web template reloading

    (cd odin-server && mvn spring-boot:run)

# Docker

    docker-compose up
    
or with rebuild    

    docker-compose up --build
    
To run interact container

    docker run --rm -it --name admin -v=odin_web-volume:/var/lib/web busybox
        
# Android Application

See also the [Odin Android App](https://github.com/ianhomer/odin-android).  

# Raspberry PI 

## Installation

Add "my.pi" domain name to /etc/hosts
        
On my.pi create .ssh directory 

    ssh pi@my.pi
    cd ~ && install -d -m 700 ~/.ssh

From **local machine** deploy SSH Key

    cat ~/.ssh/id_rsa.pub | ssh pi@my.pi 'cat >> .ssh/authorized_keys'

On my.pi

    sudo mkdir /opt/odin
    sudo chown pi /opt/odin
        
From **local machine** deploy jar 
        
    scp odin-server/target/odin-server-1.0-SNAPSHOT.jar pi@my.pi:/opt/odin
      
On my.pi 
    
    ( cd /opt/odin ; java -jar odin-server-1.0-SNAPSHOT.jar )    

# Access Server

Root

    http://localhost:8080/

Health check

    http://localhost:8080/health

# Manual Testing

Run the MIDI playground script

    mvn exec:java

Lots of combinations and choice exist to generate and receive MIDI signals, for basics I use :

* A Korg microKEY to act as MIDI in signal.  Midi Mock (in Mac App Store) is a software keyboard that drives MIDI in
* Ableton Live as a receiver for MIDI out.

For performance I use a Nord Electro 5 and Nord Lead 4.

# Static Code Analysis

## Sonar

    mvn clean verify sonar:sonar

## JavaScript

### Karma

    cd odin-server
    ./node_modules/karma/bin/karma start karma.conf.ci.js

### ESlint

    ./node_modules/eslint/bin/eslint.js src/main/js/*.js
    
# Server Development with Hot Deployment

Start spring boot server

    cd odin-server
    mvn spring-boot:run&
  
After Code change, rebuild in IDEA with ⌘ fn-F9 and server will restart with new classes.

After react.js scripts change,

    mvn frontend:webpack
  
Stop spring boot server
  
    fg

And ctrl-c.
  
# Testing

To test a single test from command line

    mvn test -Dtest=LogCaptureTest -pl log-capture/
    
# Site
    
Generate site, including javadocs with
    
    mvn site
        
# Debug

    cd odin-server
    
    