(defproject us.physion/clojure-query "1.0.0-SNAPSHOT"
  :description "Clojure query performance"

  :dependencies [[org.clojure/clojure "1.3.0"]
                 [korma "0.3.0-beta11"]
                 [org.clojars.maravillas/korma.incubator "0.1.1-SNAPSHOT"]
                 [com.h2database/h2 "1.3.168"]
                 [lobos "1.0.0-SNAPSHOT"]
                 [criterium "0.3.1"]
                 ]

  :profiles {:dev
             {:plugins [[lein-midje "2.0.1"]]

              :dependencies [[midje "1.4.0" :exclusions [org.clojure/clojure]]
                             [com.stuartsierra/lazytest "1.2.3"]]}}

  :repositories {"stuartsierra-releases" "http://stuartsierra.com/maven2"})
