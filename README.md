# DevDay2019-Performance-Tuning
DevDay2019 for Performance Tuning

[![Actions Status](https://github.com/doanthuyan/DevDay2019-Performance-Tuning/workflows/Java%20CI/badge.svg)](https://github.com/doanthuyan/DevDay2019-Performance-Tuning/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=doanthuyan_DevDay2019-Performance-Tuning&metric=alert_status)](https://sonarcloud.io/dashboard?id=doanthuyan_DevDay2019-Performance-Tuning)

# Kick up and run

## 1. Run with default setting (local DB, default ports)
    
    mvn clean spring-boot:run
## 2. Run with external properties file
    
    mvn spring-boot:run -Dspring.config.location="your_application.properties"
    

## 3. Init data on local

    mvn clean spring-boot:run -PinitData
    
## 4. Init data on server

    mvn spring-boot:run -Dspring.config.location="profiles/groupX_application.properties" -Dspring-boot.run.arguments="--spring.datasource.initialize=true,--spring.datasource.initialization-mode=always"

Or

    java -jar devday2019project-0.0.1-SNAPSHOT.jar --spring.config.location=profiles/groupX_application.properties --spring.datasource.initialize=true --spring.datasource.initialization-mode=always 
    
## 5. DevOps

Build .jar

    mvn clean install
    
Copy .jar file to server
    
Run on server with specific profile

    java -jar devday2019project-0.0.1-SNAPSHOT.jar --spring.config.location=groupX_application.properties
    
__X: is your group number__

## 6. Swagger

http://host:port/swagger-ui.html#/

__DevOps: Please ask your supporter for team's properties files__
# Book external source
- https://api.itbook.store
- https://openlibrary.org/dev/docs/api/search
- https://www.panmacmillan.com/panmac/api-documentation
- http://api.bookmooch.com/api/search?db=bm&o=json&txt=java
- https://discover.libraryhub.jisc.ac.uk/search?format=json&title=java

