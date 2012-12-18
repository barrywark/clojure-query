(ns us.physion.clojure-query.entities
  (:require [korma.core :refer [defentity entity-fields has-one has-many table belongs-to]])
  (:require [korma.incubator.core :refer [many-to-many]]))


;; Users and groups
(declare groups users projects experiments protocols epochs)

(defentity groups
  (entity-fields :groupname)
  (has-one users) ;;administrator
  (many-to-many users :users_groups))


(defentity users
  (entity-fields :username)
  (many-to-many groups :users_groups))

(defentity owners
  (table :users)
  (entity-fields :username)
  (many-to-many groups :users_groups))

(defentity users_groups
  (has-one users)
  (has-one groups))

;; TODO add last sync sequence to each table


;; Annotations
(defentity keywords
  (entity-fields :tag)
  (has-one owners)
  (many-to-many projects :projects_keywords)
  (many-to-many experiments :experiments_keywords))

(defentity projects_keywords
  (has-one projects)
  (has-one keywords)
  (has-one users))

(defentity experiments_keywords
  (has-one experiments)
  (has-one keywords)
  (has-one users))

(defentity epochs_keywords
  (has-one epochs)
  (has-one keywords)
  (has-one users))

(defentity properties
  (entity-fields :key :type :string-value :int-value :double-value)
  (has-one users)
  (many-to-many epochs :epochs_properties))

(defentity epochs_properties
  (has-one epochs)
  (has-one properties)
  (has-one owners))
;
;;;Subselects can be used as entities too!
;;; Use these to easily sub-select owner annotations, for example
;;(defentity subselect-example
;;  (table (subselect users
;;           (where {:active true}))
;;    :activeUsers))
;
;; Entities
;; TODO factor common entity in function/macro?

(defentity protocol
  (has-many experiments))

(defentity projects
  (entity-fields :uuid)
  (has-one owners) ;; owner
  (many-to-many experiments :projects_experiments)
  (many-to-many keywords :projects_keywords))

(defentity experiments
  (entity-fields
    :uuid
    :startdate
    :purpose)
  (has-one owners) ;;owner
  (belongs-to protocols) ;;protocol
  (has-many epochs)
  (many-to-many projects :projects_experiments)
  (many-to-many keywords :experiments_keywords))

(defentity projects_experiments
  (has-one projects)
  (has-one experiments))


(defentity epochs
  (entity-fields :uuid :startdate :start-tz-offset :enddate :end-tz-offset)

  (has-one owners) ;;owner

  (belongs-to experiments)

  (many-to-many keywords :epochs_keywords)
  (many-to-many properties :epochs_properties))
