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
            )
  )

(def version {
              :name "well_feedit"
              :version "1.0.0"
              :url "https://github.com/simmel/well_feedit/"
              }
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
(defn get-comment-url [entry] (last (get-content-urls entry)))

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
      (println (format "%s Failed, exception: %s" url error))
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

(defn -main [& args]
  (defn app [req]
    (let [
          request (str
                    (subs (req :uri) 1)
                    (let [qs (req :query-string)]
                      (if (nil? qs)
                        ""
                        (str "?" qs)
                        )
                      )
                    )
          reply (get-well-feedit request)
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
  (org.httpkit.server/run-server app {:port 8080})
          )
