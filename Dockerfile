FROM adoptopenjdk/openjdk11:alpine-jre
ADD target/spring-boot-rest-1.jar api.jar
ENTRYPOINT ["java","-jar","/api.jar"]
