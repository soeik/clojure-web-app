    FROM openjdk:8u181-alpine3.8

    WORKDIR /

    COPY target/hs-app-standalone.jar hs-app-standalone.jar
    EXPOSE 9000

    CMD java -jar hs-app-standalone.jar
