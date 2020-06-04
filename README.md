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
* uses the `old.reddit.com` domain which still is better than the regular
  site.

### TODO

* [ ] Add usage
* [ ] Convert to using `lein`. `deps.edn` was fun to try but it's just not
  ready.
* [ ] Start using jib for building the container
* [ ] Add structured logging
* [ ] Move back to compojure, again. [It can be used.](https://github.com/weavejester/compojure/wiki/Routes-In-Detail#matching-the-uri)
* [ ] Not to be that person, but add tests (even though I failed to use TDD yet
  again)
