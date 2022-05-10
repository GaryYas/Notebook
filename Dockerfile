FROM openjdk:11
add target/knime-0.0.1-SNAPSHOT.jar notebook-app.jar
ENTRYPOINT ["java", "-jar", "notebook-app.jar"]