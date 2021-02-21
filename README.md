# spring-boot-rest
Spring boot REST service using docker-compose
The mongo image will create a database folder on your local machine ~/data/db:/data/db
## To run
while in working folder or run the deply.sh shell script.
```
mvn clean package -Dmaven.test.skip=true
docker build -t springboot .
docker-compose up -d
```
## Swagger
* http://localhost:8080/swagger-ui/
* http://localhost:8080/api-docs
* http://localhost:8080/api-docs.yaml
