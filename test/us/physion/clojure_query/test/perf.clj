(ns us.physion.clojure-query.test.perf
  (:use [us.physion.clojure-query.entities])
  (:require [us.physion.clojure-query [db :as db] ])
  (:require [korma.core :refer [insert values select join where fields sqlfn aggregate]])
  (:require [korma.incubator.core :refer [with]])
  (:require [lobos core migrations config])
  (:require [criterium.core :refer [bench with-progress-reporting]]))

(defn random-uuid []
  (. java.util.UUID randomUUID))


(defn add-experiment-keywords [n experiment-id owner-id]
  (dotimes [i 5]
    (let [keyword-id (-> (insert keywords (values {:tag (format "experiment-tag-%d" i)})) vals first)]
      (printf (format "keyword-id: %d" keyword-id))
      (insert experiments_keywords (values {:experiments_id experiment-id
                                                :keywords_id keyword-id
                                                :users_id owner-id})))))

(defn build-experiments [n project-id owner-id]
  (dotimes [i n]
    (let [experiment-id (-> (insert experiments (values {:users_id owner-id
                                                             :startdate (java.sql.Timestamp. 1)
                                                             :tz-offset 0})) vals first)]
      ;(add-experiment-keywords n experiment-id owner-id)
      (insert projects_experiments (values {:projects_id project-id
                                                :experiments_id experiment-id}))
      )))

(defn add-project-keywords [n project-id owner-id]
  (dotimes [i 5]
    (let [keyword-id (-> (insert keywords (values {:tag (format "project-tag-%d" i)})) vals first)]
      (insert projects_keywords (values {:projects_id project-id
                                             :keywords_id keyword-id
                                             :users_id owner-id})))))

(defn build-projects [n]
  (lobos.core/migrate)
  (let [owner-id (-> (insert users (values {:username "big-owner"})) vals first)]
    (dotimes [i n]
      (let [project-id (-> (insert projects (values {:uuid (random-uuid) :users_id owner-id })) vals first)]
        (add-project-keywords n project-id owner-id)
        (build-experiments n project-id owner-id)
        ))))






(defn experiments-query []
  (select projects
    (aggregate (count :*) :project-count))

  (select experiments
    (where (< :startdate (sqlfn now))))

  (select keywords
    (where (like :tag "project-tag-10"))
    (aggregate (count :*) :keyword-count))

  (select experiments
    (join :inner projects_experiments (= :projects_experiments.experiments_id :experiments.id))
    (join :inner projects (= :projects.id :projects_experiments.id))

    (join :inner projects_keywords (= :projects_keywords.projects_id :projects.id))
    (join :inner keywords (= :projects_keywords.keywords_id :keywords.id))

    (where (like :keywords.tag "project-tag-1"))

    (aggregate (count :*) :exp-count)
    )
  )

(defn bench-query []
  (bench (experiments-query)))
