(ns se.soy.well_feedit.core
  (:gen-class)
  (:require [
             buran.core :refer [
                                combine-feeds
                                consume
                                consume-http
                                filter-entries
                                produce
                                shrink
                                sort-entries-by
                                ]
             ]
            [org.httpkit.server]
            [org.httpkit.client]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            )
  (:use
    [compojure.route :only [files not-found]]
    [compojure.core :only [defroutes GET]]
    )
  )
(def ^:dynamic *variant* "https://www.reddit.com")

(def version (let
               [
                name "well_feedit"
                version
                (let [pom-properties
                      (with-open
                        [pom-properties-reader
                         (io/reader
                           (io/resource
                             (format "META-INF/maven/%1$s/%1$s/pom.properties" name)
                             ))]
                        (doto (java.util.Properties.)
                          (.load pom-properties-reader)))]
                  (get pom-properties "version"))
                ]
               {
                :name name
                :version version
                :url "https://github.com/simmel/well_feedit/"
                }
               )
  )

(def user-agent (let
                  [rome (.getPackage com.rometools.rome.io.XmlReader)]
                  (format "%s/%s (+%s) %s/%s"
                          (version :name)
                          (version :version)
                          (version :url)
                          (.getImplementationTitle rome)
                          (.getImplementationVersion rome)
                          )
                  )
  )

(def http-kit-options {
              :timeout 1000             ; ms
              :user-agent user-agent
              }
  )

(defn get-content-urls [entry]
  (map (fn [a] (second a))
       (rest
         (re-seq #"href=\"(http.*?)\""
                 (->
                   entry
                   :contents
                   first
                   :value
                   )
                 )
         )
       )
  )

(defn get-original-url [_ entry] (first (get-content-urls entry)))
(defn get-comment-url [entry] (clojure.string/replace (last (get-content-urls entry)) #"^https://www.reddit.com" *variant*))

(defn update-link [entry]
  (update
    entry
    :link
    get-original-url
    entry
    )
  )

; FIXME Merge these into one function with different argument arities?
(defn update-comment-links [link entry]
  (map #(assoc %
               :rel "replies"
               :type "text/html"
               :href (get-comment-url entry)
               )
       link)
  )

(defn update-comment-link [entry]
  (update
    entry
    :links
    update-comment-links
    entry
    )
  )

(defn remove-content [entry]
  (dissoc entry :contents)
  )

(defn fix-reddit-entry [entry]
  (->
    entry
    update-comment-link
    update-link
    ;; Has to be last since update-comment-link and update-link depends on
    ;; content
    remove-content
    )
  )

(defn fix-reddit-feed [feed]
  (produce (shrink (update feed :entries #(map fix-reddit-entry %))))
  )

(defn get-reddit-feed [url]
  (let [
        {:keys [
                body
                error
                headers
                status
                ]
         :as resp
         } @(
             org.httpkit.client/get
             url
             http-kit-options
             )
        ]
    (if error
      (log/errorf error "%s Failed" url)
      body
      )
    )
  )

(defn get-well-feedit [url]
  (->
    url
    get-reddit-feed
    consume
    fix-reddit-feed
    )
  )

(defn get-real-ip [req]
  (if (contains? (req :headers) "x-forwarded-for")
    (get-in req [:headers "x-forwarded-for"])
    (req :remote-addr)
    )
  )

(defn get-variant-url [server-name]
  (cond
    (clojure.string/starts-with? server-name "old.") "https://old.reddit.com"
    (clojure.string/starts-with? server-name "teddit.") "https://teddit.net"
    :default *variant*
    )
  )

(defn -main [& args]
  (defroutes app
             (GET "/" [:as req]
                  ;; 1.2.3.4 well_feed.it - [27/May/2020:13:33:37 +0000] "GET /netsec.atom HTTP/1.1" 200 2642 "-" "well_feedit/1.0.0 (+https://github.com/simmel/well_feedit/) rome/1.12.0"
                  (log/infof "%s %s %s %s %s" (get-real-ip req) (req :server-name) (req :request-method) (req :uri) (get-in req [:headers "user-agent"]))
                  {
                   :status  200
                   :headers {"Content-Type" "text/plain; charset=UTF-8"}
                   :body    "lol meow dis is how we do it\n"
                   }
                  )
             (GET "/:uri{.+}" [uri :as req]
                  ;; 1.2.3.4 well_feed.it - [27/May/2020:13:33:37 +0000] "GET /netsec.atom HTTP/1.1" 200 2642 "-" "well_feedit/1.0.0 (+https://github.com/simmel/well_feedit/) rome/1.12.0"
                  (log/infof "%s %s %s %s %s" (get-real-ip req) (req :server-name) (req :request-method) (req :uri) (get-in req [:headers "user-agent"]))
                  (let [
                        request (str
                                  "https://www.reddit.com/"
                                  uri
                                  (let [qs (req :query-string)]
                                    (if (nil? qs)
                                      ""
                                      (str "?" qs)
                                      )
                                    )
                                  )
                        reply (if (= request "")
                                nil
                                (binding [*variant* (get-variant-url (req :server-name))]
                                  (get-well-feedit request)
                                  )
                                )
                        ]
                    (cond
                      (and
                        (instance? clojure.lang.PersistentArrayMap reply)
                        (contains? reply :error)
                        )
                      {
                       :status  500
                       :headers {"Content-Type" "text/plain; charset=UTF-8"}
                       :body    "Internal Server Error\n"
                       }

                      :else
                      {
                       :status  200
                       :headers {"Content-Type" "application/atom+xml; charset=UTF-8"}
                       :body    reply
                       }
                      )
                    )
                  )
             (not-found "<p>Page not found.</p>")
  )
  (org.httpkit.server/run-server app {:port 8080})
  (log/info "Server is up!")
  )
