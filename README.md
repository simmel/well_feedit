well\_feedit
---

An Atom 1.0 "prettifying proxy" for reddits "RSS" (it's really Atom) feed to
make it more readable (primarily on [Miniflux](https://miniflux.app/), but
works for anything else as well) and actually uses the Atom standard to it's
fullest.

### What it does

well\_feedit currently:
* on each entry:
  * Extracts the real URL from the `content` element on each entry and updates
    the `link` element to that so that your feed reader can fetch the original
    content if it can and want.
  * Adds a `link` element with `rel` `replies` which goes to the reddit
    comments.
  * Then remove the `content` entry since it's just data duped from other
    elements.
* If you visit it on a subdomain which starts with `old.`, e.g.
  https://old.well-feedit.io/r/netsec/new.rss, it uses the `old.reddit.com`
  domain which still is better than the regular site on a computer but right
  now the regular site is better on mobile.

### Usage

#### Run standalone
Until I've fixed the Github Action and upload the uberjar to a tag^Wrelease,
you have do build it your self:
```terminal
$ git clone https://github.com/simmel/well_feedit.git
$ cd well_feedit
$ lein uberjar
$ java -jar target/uberjar/*-standalone.jar
[...]
[main] INFO  se.soy.well_feedit.core - Server is up!
$ curl localhost:8080/r/netsec/new.rss
<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" xmlns:dc="http://purl.org/dc/elements/1.1/">
  <title>newest submissions : netsec</title>
  <link rel="alternate" type="text/html" href="https://www.reddit.com/r/netsec/new/" />
[...]
```

#### Run as a container
```terminal
$ docker run --rm -it -p 8080:8080  well_feedit:latest
[...]
[main] INFO  se.soy.well_feedit.core - Server is up!
$ curl localhost:8080/r/netsec/new.rss
<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" xmlns:dc="http://purl.org/dc/elements/1.1/">
  <title>newest submissions : netsec</title>
  <link rel="alternate" type="text/html" href="https://www.reddit.com/r/netsec/new/" />
[...]
```

### TODO

* [X] Add usage
* [X] Convert to using `lein`. `deps.edn` was fun to try but it's just not
  ready.
* [ ] [Create a release](https://github.com/actions/create-release)
  * [ ] [Upload uberjar as an asset](https://github.com/actions/upload-release-asset/)
  * [ ] ["Upload" Docker container as an asset](https://docs.github.com/en/actions/language-and-framework-guides/publishing-docker-images)
* [X] Add support for logging X-F-F
* [X] Add support for logging X-F-F
* [X] Start running it with JDK11
* [X] Start using jib for building the container
* [ ] Add structured logging
* [X] Move back to compojure, again. [It can be used.](https://github.com/weavejester/compojure/wiki/Routes-In-Detail#matching-the-uri)
* [ ] Not to be that person, but add tests (even though I failed to use TDD yet
  again)
