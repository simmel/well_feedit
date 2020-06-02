FROM clojure:tools-deps-1.10.1.536-slim-buster@sha256:842b105a0df85bf19aee6f44ea791b6306e6de7cf11989a110e47a659c1969cb as builder
WORKDIR /usr/src

COPY . .

RUN ["clj", "-A:uberjar"]

FROM adoptopenjdk/openjdk8:jdk8u252-b09-alpine-slim@sha256:3de6dfd82768fe2b81c6d4609f041b3c8b75e15f9917fa05fec5cb07b30f6a94 as prod

COPY --from=builder target/*-standalone.jar /app.jar

USER 1000

EXPOSE 8080

CMD ["java", "-Xmx20m", "-Xms20m", "-jar", "/app.jar"]
