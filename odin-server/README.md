# Useful Developer Commands

Quick install

    mvn install -P quick

Test some tests with debug logging on

    mvn test -Dtest=MainControllerTest,CompositionControllerTest
    
Test some tests with debug logging on and use a specific log configuration file for
spring context

    mvn test -Dtest=MainControllerTest,SystemControllerTest,CompositionControllerTest \
      -Dlogging.config=src/main/resources/logback-debug.xml


      -Dlogback.configurationFile=src/main/resources/logback-debug.xml


Enable debugger with debug logging

    java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 \
      -Dlogback.debug=true -Dlogback.configurationFile=src/main/resources/logback-debug.xml \   
      -jar odin-server/target/odin-server-1.0-SNAPSHOT.jar
