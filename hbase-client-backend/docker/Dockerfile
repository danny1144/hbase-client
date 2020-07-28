FROM  10.192.30.61:5000/library/openjdk:8u181-jdk-alpine-font
MAINTAINER wang.liu <wang.liu@siemens.com>

# We added a VOLUME pointing to "/tmp" because that is where a Spring Boot application creates working directories for Tomcat by default. The effect is to create a temporary file on your host under "/var/lib/docker" and link it to the container under "/tmp".
VOLUME /tmp

ENV APP_ARTIFACT_PACKAGE="*.jar"
ADD ./$APP_ARTIFACT_PACKAGE /
RUN rm -r -f *-docker-info.jar
RUN rm -r -f *-pg.jar
RUN sh -c 'touch /$APP_ARTIFACT_PACKAGE'

# JVM options
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"

# Set the command options for Spring Boot if any
ENV JAVA_OPTS_SPRING_BOOT=""

# Using docker run with '-e' option if you want to switch the Spring Boot profile,
# e.g.: -e SPRING_BOOT_CMD_OPTS="--spring.profiles.active=prod" to inject it as the command option for Spring Boot running.
CMD java $JAVA_OPTS $JAVA_OPTS_SPRING_BOOT -Djava.security.egd=file:/dev/./urandom -jar `ls $APP_ARTIFACT_PACKAGE` $SPRING_BOOT_CMD_OPTS
