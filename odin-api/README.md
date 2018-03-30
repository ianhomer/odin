# Backend

Quick install

    mvn install -P quick


## Docker

    docker build -t odin-api .
    docker run --name odin-api -p 8081:8080 -d odin-api
    
### Recycle with rebuilt core

    cd ../odin-core && mvn clean install -P quick && cd ../odin-api && \
      mvn install -P quick        && \
      docker stop odin-api        && \
      docker rm odin-api          && \
      docker build -t odin-api .  && \
      docker run --name odin-api -p 8081:8080 -d odin-api
      
# Test
    
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