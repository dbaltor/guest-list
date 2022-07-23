FROM maven:3.8.3-openjdk-17-slim AS BUILD

COPY . /opt/app
WORKDIR /opt/app
RUN mvn package

FROM openjdk:17.0.1-jdk-slim
ENV JAR='guestlist-1.0-SNAPSHOT.jar'
COPY --from=BUILD /opt/app/target/classes /opt/app/classes
COPY --from=BUILD /opt/app/target/${JAR} /opt/app/${JAR}
WORKDIR /opt/app

ENTRYPOINT ["/bin/sh", "-c", "java -Dspring.profiles.active=prod -jar ${JAR}"]

