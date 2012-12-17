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

;(against-backgroud [ (before :contents (do
;                                         (db/start-server)
;                                         (lobos.core/migrate)))
;
;                     (before :facts (kdb/transaction
;                                      (let [user-id (first (vals (korma/insert users (korma/values {:username "test"}))))
;                                            project-id (first (vals (korma/insert projects (korma/values {:users_id user-id}))))])))
;
;
;                     (after :facts (kdb/transaction
;                                     (korma/delete groups)
;                                     (korma/delete users)
;                                     (korma/delete users_groups)
;                                     (korma/delete projects)
;                                     ))
;                     ]
;
;(fact "projects have uuids"
;  false => true)
;  )

(against-background [ (before :contents (do
                                          (db/start-server)
                                          (lobos.core/migrate)))

                      (before :facts (kdb/transaction
                                       (let [group-id (first (vals (korma/insert groups (korma/values {:groupname "group"}))))
                                             user-id (first (vals (korma/insert users (korma/values {:username "test"}))))
                                             project-id (first (vals (korma/insert projects (korma/values {:users_id user-id}))))
                                             experiment-id (first (vals (korma/insert experiments (korma/values {:users_id user-id
                                                                                                                 :startdate (java.sql.Timestamp. 1)
                                                                                                                 :tz-offset -3}))))
                                             keyword-id (first (vals (korma/insert keywords (korma/values {:tag "my-tag"}))))]

                                         (korma/insert users_groups (korma/values {:groups_id group-id
                                                                                   :users_id  user-id}))
                                         (korma/insert projects_experiments (korma/values {:projects_id project-id
                                                                                           :experiments_id experiment-id}))
                                         (korma/insert projects_keywords (korma/values {:projects_id project-id
                                                                                        :keywords_id keyword-id
                                                                                        :users_id user-id}))
                                         )))

                      (after :facts (kdb/transaction
                                      (korma/delete groups)
                                      (korma/delete users)
                                      (korma/delete users_groups)
                                      (korma/delete projects)
                                      (korma/delete keywords)
                                      (korma/delete projects_experiments)
                                      (korma/delete projects_keywords)))
                      ]
  (fact "experiments have projects with keywords"

;    (->
;      (korma/select experiments
;        (korma/fields :experiments.uuid)
;        (korma2/with projects (korma2/with keywords)))
;      first
;      :projects)
    (korma/select experiments
      (korma/fields [:experiments.id :expid] [:projects.id :pid])
      (korma/join projects_experiments (= :projects_experiments.projects_id :experiments.id))
      (korma/join projects (= :projects.id :projects_experiments.id))
      (korma/join projects_keywords (= :projects_keywords.projects_id :projects.id))
      (korma/join keywords (= :projects_keywords.keywords_id :keywords.id))
      (korma/where (= :keywords.tag "my-tag"))) =not=> empty?)
  )
