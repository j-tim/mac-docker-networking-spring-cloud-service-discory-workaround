FROM anapsix/alpine-java
MAINTAINER "tim@jtim.nl"

# Directory where our app will be located
ENV APP_NAME=hello-world-service \
    APP_USER=nobody \
    APP_HOME=/opt/hello-world-service

ENV JAVA_OPTS="\
  -Xmx128m -Xms64m \
  -agentlib:jdwp=transport=dt_socket,server=y,address=5005,suspend=n \
"

WORKDIR ${APP_HOME}
RUN chown ${APP_USER}: .

USER $APP_USER

# Copy the snapshot or release artifact into the container and rename the file to alert-matching.jar
COPY ./target/${APP_NAME}-*.jar ./${APP_NAME}.jar

# Spring Boot Embedded Tomcat: 8080
# Remote debugging: 5005
EXPOSE 8080 5005

ENTRYPOINT exec java $JAVA_OPTS -jar ${APP_NAME}.jar
