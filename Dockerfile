FROM openjdk:11-jdk-alpine
ADD target/Spring-Batch-Example-1-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]