# Server Development with Hot Deployment

Start spring boot server

    (cd odin-api && mvn spring-boot:run&)
    
Then open odin-frontend/public/index.html in browser
  
Java code changes can be applied by rebuilding in IDEA with ⌘ fn-F9.  The server will restart with 
new classes.   Front end changes can be made direct will take immediate effect.

Stop spring boot server
  
    fg

And ctrl-c.


# Increase logging levels at runtime

    curl -d '{"configuredLevel":"DEBUG"}' -H "Content-Type: application/json" \
      http://localhost:8080/actuator/loggers/com.purplepip
      
    curl -d '{"configuredLevel":"DEBUG"}' -H "Content-Type: application/json" \
      http://localhost:8080/actuator/loggers/org.springframework.security
