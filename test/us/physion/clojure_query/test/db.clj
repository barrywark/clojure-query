(ns us.physion.clojure-query.test.db
  (:require [us.physion.clojure-query [db :as db]])
  (:require [lobos core migrations config])
  (:use [midje.sweet]))

(against-background [ (before :contents (lobos.core/migrate))
                      ]
  (fact "we have a test db"
  db/testdb =not=> nil?))
