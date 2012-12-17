(ns us.physion.clojure-query.test.query
  (:use [us.physion.clojure-query.entities])
  (:require [us.physion.clojure-query [db :as db] ])
  (:require (korma [core :as korma] [db :as kdb] ))
  (:require [korma.incubator [core :as korma2]])
  (:use [midje.sweet])
  (:require [lobos core migrations config]))

(against-background [ (before :contents (do
                                          (db/start-server)
                                          (lobos.core/migrate)))

                      (before :facts (kdb/transaction
                                       (let [group-id (korma/insert groups (korma/values {:groupname "group"}))
                                             user-id (korma/insert users (korma/values {:username "test"}))]

                                         (korma/insert users_groups (korma/values {:groups_id (first (vals group-id))
                                                                                   :users_id  (first (vals user-id))})))))

                      (after :facts (kdb/transaction
                                      (korma/delete groups)
                                      (korma/delete users)
                                      (korma/delete users_groups)))
                      ]
  (fact "users have groups"
    (->
      (korma/select users
        (korma2/with groups))
      first
      :groups) =not=> empty?)
  )
