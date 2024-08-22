FROM openjdk:17-alpine
VOLUME /tmp
ADD ./servicio-balizas.jar servicio-balizas.jar
ENTRYPOINT ["java","-jar","/servicio-balizas.jar"]
