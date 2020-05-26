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
            )
  )

;; (defn -main [& args]
;;   (do (println "lol")
;;       )
;;   )
;;

(def f (consume-http {:from "https://soy.se/netsec.atom"}))

; FIXME Merge these into one function with different argument arities?
(defn update-comment-link [entry]
  (update
    entry
    :links
    update-comment-links
    entry
    )
  )

(defn update-comment-links [link entry]
  (map #(assoc %
               :rel "replies"
               :type "text/html"
               :href (get-comment-url entry)
               )
       link)
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

(defn get-original-url [entry] (first (get-content-urls entry)))
(defn get-comment-url [entry] (last (get-content-urls entry)))
