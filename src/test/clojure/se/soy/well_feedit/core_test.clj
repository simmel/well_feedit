(ns se.soy.well_feedit.core_test
  (:use org.httpkit.fake)
  (:require
    [se.soy.well_feedit.core :refer :all]
    [clojure.test :refer :all]
            ))

(deftest a-test
         (testing "Testing is"
                  (with-fake-http ["https://reddit.com/r/netsec/new.rss" "testing http"]
                                  (let [netsec (get-reddit-feed
                                                 "https://reddit.com/r/netsec/new.rss")]
                                    (is (= "testing http" netsec))
                                    ))
                  )
         )
