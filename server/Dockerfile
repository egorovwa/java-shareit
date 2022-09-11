FROM amazoncorretto:11-alpine-jdk
COPY ../contract/target/*.jar /contract.jar
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]