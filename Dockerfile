FROM openjdk:8-jdk-alpine
COPY target/demo-backend-0.0.1-SNAPSHOT.jar demo-backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/demo-backend-0.0.1-SNAPSHOT.jar"]