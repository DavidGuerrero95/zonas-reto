FROM openjdk:12
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Zonas.jar
ENTRYPOINT ["java","-jar","/Zonas.jar"]