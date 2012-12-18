(ns us.physion.clojure-query.entities
  (:require [korma.core :as korma]) ;;:refer [defentity entity-fields has-one]])
  (:require [korma.incubator.core :as korma2]))


;; Users and groups
(declare groups users projects experiments protocols)

(korma/defentity groups
  (korma/entity-fields :groupname)
  (korma/has-one users) ;;administrator
  (korma2/many-to-many users :users_groups))


(korma/defentity users
  (korma/entity-fields :username)
  (korma2/many-to-many groups :users_groups))

(korma/defentity users_groups
  (korma/has-one users)
  (korma/has-one groups))

;; TODO add last sync sequence to each table


;; Annotations
(korma/defentity keywords
  (korma/entity-fields :tag)
  (korma/has-one users)
  (korma2/many-to-many projects :projects_keywords)
  (korma2/many-to-many experiments :experiments_keywords))

(korma/defentity projects_keywords
  (korma/has-one projects)
  (korma/has-one keywords)
  (korma/has-one users))

(korma/defentity experiments_keywords
  (korma/has-one experiments)
  (korma/has-one keywords)
  (korma/has-one users))

;
;
;(defentity properties
;  (entity-fields :key :value)
;  (has-one users)
;
;  (many-to-many epochs :epochs_properties
;    {:lfk :properties_id
;     :rfk :epochs_id}))
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

(korma/defentity protocol
  (korma/has-many experiments))

(korma/defentity projects
  (korma/entity-fields :uuid)
  (korma/has-one users) ;; owner
  (korma2/many-to-many experiments :projects_experiments)
  (korma2/many-to-many keywords :projects_keywords))

(korma/defentity experiments
  (korma/entity-fields
    :uuid
    :startdate
    :purpose)
  (korma/has-one users) ;;owner
  (korma/belongs-to protocols) ;;protocol
  (korma2/many-to-many projects :projects_experiments)
  (korma2/many-to-many keywords :experiments_keywords))

(korma/defentity projects_experiments
  (korma/has-one projects)
  (korma/has-one experiments))

;
;(defentity epochs
;  (entity-fields :startdate)
;  (has-one users) ;;owner
;  (has-one protocol)
;  (has-one experiments)
;  (many-to-many keywords :epochs_keywords
;    {:lfk :epochs_id
;     :rfk :keywords_id})
;  (many-to-many keywords :epochs_properties
;    {:lfk :epochs_id
;     :rfk :properties_id}))
