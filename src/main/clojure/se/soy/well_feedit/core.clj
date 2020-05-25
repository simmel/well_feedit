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
