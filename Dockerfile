    FROM openjdk:8u181-alpine3.8

    WORKDIR /

    COPY target/hs-api.jar hs-api.jar
    EXPOSE 9000

    CMD java -jar hs-api.jar
