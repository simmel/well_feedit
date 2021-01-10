FROM clojure:lein-2.9.3-slim-buster@sha256:a0149bcfda2b2dc22414ca4142b97a5e3bfe2c7052ef4f1e6f3bc7aa77ea6de4 as builder
WORKDIR /usr/src

COPY project.clj .

RUN ["lein", "uberjar"]

COPY src src

RUN ["lein", "test"]

RUN ["lein", "uberjar"]

FROM adoptopenjdk/openjdk11:jre-11.0.7_10-alpine@sha256:cbd20379d141de45148f6e2f2be388f8cbb3a5211eaaa389930a3a80b56d95ee as prod

COPY --from=builder /usr/src/target/uberjar/*-standalone.jar /app.jar

USER 1000

EXPOSE 8080

CMD ["java", "-Xmx40m", "-Xms40m", "-jar", "/app.jar"]
