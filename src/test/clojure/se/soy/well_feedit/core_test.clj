(ns se.soy.well_feedit.core_test
  (:use org.httpkit.fake)
  (:require
    [se.soy.well_feedit.core :refer :all]
    [clojure.test :refer :all]
            ))

(deftest a-test
         (testing "Testing is"
                  (let [
                        netsec-original (slurp "src/test/resources/netsec-original.atom")
                        netsec-fixed (slurp "src/test/resources/netsec-fixed.atom")
                        ]
                  (with-fake-http ["https://www.reddit.com/r/netsec/new.rss" netsec-original]
                                  (let [netsec (get-well-feedit
                                                 "https://www.reddit.com/r/netsec/new.rss")]
                                    (is (clojure.string/includes? netsec "<link rel=\"replies\" type=\"text/html\" href=\"https://www.reddit.com/r/netsec/comments/kzal3u/unvalidated_user_input_in_ms_sharepoint_2019/\" />"))
                                    ))
                  )
                  )
         )
