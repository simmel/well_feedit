(defproject well_feedit "3.0.1"
  :description "A \"prettifying Atom proxy\" for reddits \"RSS\" feeds — for better reading"
  :url "https://github.com/simmel/well_feedit"
  :license {:name "ISC License"
            :url "https://www.isc.org/downloads/software-support-policy/isc-license/"}
  :source-paths ["src/main/clojure"]
  :resource-paths ["src/main/resources"]
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 [buran "0.1.4"]
                 [http-kit "2.4.0-alpha6"]
                 [compojure "1.6.1"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 ]
  :main ^:skip-aot se.soy.well_feedit.core
  :aot [se.soy.well_feedit.core]
  :target-path "target/%s"
  :release-tasks [
                  ["vcs" "assert-committed"]
                  ["change" "version"
                   "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ]
                  ;; ["vcs" "push"]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
