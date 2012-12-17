(ns lobos.helpers
  (:refer-clojure :exclude [bigint boolean char double float time])
  (:use (lobos schema)))

(defn surrogate-key [table]
  (integer table :id :auto-inc :primary-key))

(defn timestamps [table]
  (-> table
    (timestamp :updated_on)
    (timestamp :created_on (default (now)))))

(defn refer-to [table ptable cascade]
  (let [cname (-> (->> ptable name butlast (apply str)) (str "s_id") keyword)]
    (integer table cname [:refer ptable :id :on-delete (if cascade :cascade :set-null)])))


;; Defines a more helpful table macro that includes the pk
;; TODO include last sync sequence number
(defmacro tbl [name & elements]
  `(-> (table ~name)
     (surrogate-key)
     ;;(timestamps)
     ~@elements))


;; Define a UUID column type (for H2)
(defn uuid [table name & options]
  (apply column table name (data-type :uuid) options))


