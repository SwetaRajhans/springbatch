FROM openjdk:11-jre-slim
ADD Spring-Batch-Example-1-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
