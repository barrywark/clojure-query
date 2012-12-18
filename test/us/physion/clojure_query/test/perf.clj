(ns us.physion.clojure-query.test.perf
  (:use [us.physion.clojure-query.entities])
  (:require [us.physion.clojure-query [db :as db] ])
  (:require [korma.core :refer [insert values select join where fields sqlfn aggregate modifier]])
  (:require [korma.incubator.core :refer [with]])
  (:require [lobos core migrations config])
  (:require [criterium.core :refer [bench with-progress-reporting]]))

(defn random-uuid []
  (. java.util.UUID randomUUID))


(defn add-epoch-keywords [epoch-id owner-id]
  (dotimes [i 5]
    (let [keyword-id (-> (insert keywords (values {:tag (format "epoch-tag-%d" i)})) vals first)]
      (insert epochs_keywords (values {:epochs_id epoch-id
                                       :keywords_id keyword-id
                                       :users_id owner-id})))))

(defn add-epoch-properties [epoch-id owner-id]
  (dotimes [i 100]
    (let [property-id (-> (insert properties (values {:key (format "epoch-prop-%d" i)
                                                      :type 1
                                                      :int-value i})) vals first)]
      (insert epochs_properties (values {:epochs_id epoch-id
                                         :properties_id property-id
                                         :users_id owner-id})))))

(defn build-epochs [n experiment-id owner-id]
  (dotimes [i n]
    (let [epoch-id (-> (insert epochs (values {:uuid (random-uuid )
                                               :users_id owner-id
                                               :experiments_id experiment-id })) vals first)]
      (add-epoch-keywords epoch-id owner-id)
      (add-epoch-properties epoch-id owner-id)
      )))


(defn add-experiment-keywords [n experiment-id owner-id]
  (dotimes [i 5]
    (let [keyword-id (-> (insert keywords (values {:tag (format "experiment-tag-%d" i)})) vals first)]
      (insert experiments_keywords (values {:experiments_id experiment-id
                                            :keywords_id keyword-id
                                            :users_id owner-id})))))

(defn build-experiments [n project-id owner-id]
  (dotimes [i n]
    (let [experiment-id (-> (insert experiments (values {:users_id owner-id
                                                         :startdate (java.sql.Timestamp. 1)
                                                         :tz-offset 0})) vals first)]
      (insert projects_experiments (values {:projects_id project-id
                                            :experiments_id experiment-id}))

      (add-experiment-keywords n experiment-id owner-id)
      (build-epochs n experiment-id owner-id)
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
    (modifier "DISTINCT")

    (join :inner projects_experiments (= :projects_experiments.experiments_id :experiments.id))
    (join :inner projects (= :projects.id :projects_experiments.id))

    (join :inner projects_keywords (= :projects_keywords.projects_id :projects.id))
    (join :inner keywords (= :projects_keywords.keywords_id :keywords.id))

    (where (like :keywords.tag "project-tag-1"))

    (aggregate (count :*) :exp-count)
    )

  (select epochs
    (modifier "DISTINCT")

    (join :inner experiments (= :experiments_id :experiments.id))

    (join :inner projects_experiments (= :projects_experiments.experiments_id :experiments.id))
    (join :inner projects (= :projects.id :projects_experiments.id))

    (join :inner projects_keywords (= :projects_keywords.projects_id :projects.id))
    (join :inner keywords (= :projects_keywords.keywords_id :keywords.id))

    (where (like :keywords.tag "project-tag-1"))

    (join :inner epochs_properties (= :epochs.id :epochs_properties.epochs_id))
    (join :inner properties (= :epochs_properties.properties_id :properties.id))

    (where (or (and (like :properties.key "epoch-prop-10")
                 (= :properties.int-value 10))
             (and (like :properties.key "epoch-prop-1")
               (= :properties.int-value 5))))

    (aggregate (count :*) :epoch-count)
    )
  )

(defn bench-query []
  (bench (experiments-query)))
