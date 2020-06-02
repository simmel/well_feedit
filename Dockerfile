FROM clojure:tools-deps-1.10.1.536-slim-buster@sha256:842b105a0df85bf19aee6f44ea791b6306e6de7cf11989a110e47a659c1969cb as builder
WORKDIR /usr/src

COPY . .

RUN ["clj", "-A:uberjar"]
