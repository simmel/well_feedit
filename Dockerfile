FROM clojure:lein-2.9.3-slim-buster@sha256:a0149bcfda2b2dc22414ca4142b97a5e3bfe2c7052ef4f1e6f3bc7aa77ea6de4 as builder
WORKDIR /usr/src

COPY project.clj .

RUN ["lein", "uberjar"]

COPY src src

RUN ["lein", "uberjar"]

FROM adoptopenjdk/openjdk8:jdk8u252-b09-alpine-slim@sha256:3de6dfd82768fe2b81c6d4609f041b3c8b75e15f9917fa05fec5cb07b30f6a94 as prod

COPY --from=builder /usr/src/target/uberjar/*-standalone.jar /app.jar

USER 1000

EXPOSE 8080

CMD ["java", "-Xmx20m", "-Xms20m", "-jar", "/app.jar"]
