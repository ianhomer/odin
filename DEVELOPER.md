# Server Development with Hot Deployment

Start spring boot server

    cd odin-api && mvn spring-boot:run &
    
Start react app

    cd odin-frontend && yarn start    
    
(or open odin-frontend/public/index.html in browser)
  
Java code changes can be applied by rebuilding in IDEA with ⌘ fn-F9.  The server will restart with 
new classes.   Front end changes can be made direct will take immediate effect.

Stop spring boot server
  
    fg

And ctrl-c.

# Quick full build with debug logging

    cd odin-api/ && mvn install -P quick && cd - && \
    cd odin-server/ && mvn clean install -P quick && cd - && \
    java -Dlogback.debug=true -Dlogging.config=common-logging/src/main/resources/com/purplepip/common/logback-debug.xml -Dspring.profiles.active=prod -jar odin-server/target/odin-server-1.0-SNAPSHOT.jar

Note that need for "prod" profile will be removed as prod is default.

# Increase logging levels at runtime

    curl -d '{"configuredLevel":"DEBUG"}' -H "Content-Type: application/json" \
      http://localhost:8080/actuator/loggers/com.purplepip
      
    curl -d '{"configuredLevel":"DEBUG"}' -H "Content-Type: application/json" \
      http://localhost:8080/actuator/loggers/org.springframework.security
