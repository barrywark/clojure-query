(ns us.physion.clojure-query.test.perf
  (:use [us.physion.clojure-query.entities])
  (:require [us.physion.clojure-query [db :as db] ])
  (:require (korma [core :as k] [db :as kdb] ))
  (:require [korma.incubator [core :as k2]])
  (:use [midje.sweet])
  (:require [lobos core migrations config])
  (:require [criterium.core :refer [bench with-progress-reporting]]))

(defn random-uuid []
  (. java.util.UUID randomUUID))


(defn add-experiment-keywords [n experiment-id owner-id]
  (dotimes [i n]
    (let [keyword-id (-> (k/insert keywords (k/values {:tag (format "experiment-tag-%d" i)})) vals first)]
      (printf (format "keyword-id: %d" keyword-id))
      (k/insert experiments_keywords (k/values {:experiments_id experiment-id
                                                :keywords_id keyword-id
                                                :users_id owner-id})))))

(defn build-experiments [n project-id owner-id]
  (repeat n
    (let [experiment-id (-> (k/insert experiments (k/values {:users_id owner-id
                                                             :startdate (java.sql.Timestamp. 1)
                                                             :tz-offset 0})) vals first)]
      ;(add-experiment-keywords n experiment-id owner-id)
      (k/insert projects_experiments (k/values {:projects_id project-id
                                                :experiments_id experiment-id}))
      )))

(defn add-project-keywords [n project-id owner-id]
  (dotimes [i n]
    (let [keyword-id (-> (k/insert keywords (k/values {:tag (format "project-tag-%d" i)})) vals first)]
      (k/insert projects_keywords (k/values {:projects_id project-id
                                             :keywords_id keyword-id
                                             :users_id owner-id})))))

(defn build-projects [n]
  (lobos.core/migrate)
  (let [owner-id (-> (k/insert users (k/values {:username "big-owner"})) vals first)]
    (dotimes [i n]
      (let [project-id (-> (k/insert projects (k/values {:uuid (random-uuid) :users_id owner-id })) vals first)]
        (add-project-keywords n project-id owner-id)
        (build-experiments n project-id owner-id)
        ))))






(defn experiments-query []
  (k/select experiments
    (k/fields [:experiments.id :expid] [:projects.id :pid])
    (k/join projects_experiments (= :projects_experiments.projects_id :experiments.id))
    (k/join projects (= :projects.id :projects_experiments.id))
    (k/join projects_keywords (= :projects_keywords.projects_id :projects.id))
    (k/join keywords (= :projects_keywords.keywords_id :keywords.id))
    (k/where (= :keywords.tag "project-tag-1"))
    ))

(defn bench-query []
  (criterium.core/bench (experiments-query)))
