# odin API

## Backend

Quick install

    mvn install -P quick

### Stand alone

    mvn spring-boot:run

Hot reload is enabled, so application will restart if you recompile a
class (e.g. build module in IDE), e.g. Shift-Fn-Cmd-F9 in IDEA in the
window of the class that was changed, or even just touch a class, e.g.

    touch target/classes/com/purplepip/odin/api/OdinNoStoreConfiguration.class

### Docker

    docker build -t odin-api .
    docker run --name odin-api -p 8081:8080 -d odin-api

#### Recycle with rebuilt core

    cd ../odin-core && mvn clean install -P quick && cd ../odin-api && \
      mvn install -P quick        && \
      docker stop odin-api        && \
      docker rm odin-api          ; \
      docker build -t odin-api .  && \
      docker run --name odin-api -p 8081:8080 -d odin-api

## End points

Actuators at http://localhost:8080/actuator.

API Endpoints

- http://localhost:8080/api/services/system/environment
- http://localhost:8080/api/rest/sequence

## Test

Test some tests with debug logging on

    mvn test -Dtest=MainControllerTest,CompositionControllerTest

Test some tests with debug logging on and use a specific log configuration file for
spring context

    mvn test -Dtest=MainControllerTest,SystemControllerTest,CompositionControllerTest \
      -Dlogging.config=src/main/resources/logback-debug.xml \
      -Dlogback.configurationFile=src/main/resources/logback-debug.xml

Run with no auditing and debug logging

     mvn install -P quick && java -Dlogging.config=src/main/resources/logback-debug.xml \
      -Dlogback.debug=true -Dlogback.configurationFile=src/main/resources/logback-debug.xml \
      -Dspring.profiles.active=noAuditing \
      -jar target/odin-server-1.0-SNAPSHOT.jar

Enable debugger with debug logging

    mvn install -P quick && java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 \
      -Dlogback.debug=true -Dlogback.configurationFile=src/main/resources/logback-debug.xml \
      -jar target/odin-server-1.0-SNAPSHOT.jar

