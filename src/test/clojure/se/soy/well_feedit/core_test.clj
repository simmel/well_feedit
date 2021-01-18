(ns se.soy.well_feedit.core_test
  (:use org.httpkit.fake)
  (:require
    [se.soy.well_feedit.core :refer :all]
    [clojure.test :refer :all]
    [org.httpkit.client :as http]
            ))

(deftest a-test
         (testing "Testing is"
                  (with-fake-http ["https://reddit.com/r/netsec/new.rss" "testing http"]
                                  (let [netsec (http/get
                                                 "https://reddit.com/r/netsec/new.rss")]
                                    (is (= "testing http" (:body @netsec)))
                                    ))
                  )
         )
