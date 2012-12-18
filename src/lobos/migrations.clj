(ns lobos.migrations
  (:refer-clojure :exclude [alter drop bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]]
          core
          schema
          helpers)))

(defmigration add-users-table
  (up [] (create
           (tbl :users
             (varchar :username 100) ;:unique
             (check :username (> (length :username) 1)))))
  (down [] (drop (table :users))))

(defmigration add-groups-table
  (up [] (create
           (tbl :groups
             (varchar :groupname 100) ;:unique
             (check :groupname (> (length :groupname) 0)))))
  (down [] (drop (table :groups))))

(defmigration add-users-groups-table
  (up [] (create
           (tbl :users_groups
             (refer-to :users false)
             (refer-to :groups false)
             )))
  (down [] (drop (table :users_groups))))

(defmigration add-projects-table
  (up [] (create
           (tbl :projects
             (uuid :uuid)
             (refer-to :users false))))
  (down [] (drop (table :projects))))

(defmigration add-protocols-table
  (up [] (create
           (tbl :protocols
             (uuid :uuid))))
  (down [] (drop (table :protocols))))


(defmigration add-experiments-table
  (up [] (create
           (tbl :experiments
             (uuid :uuid)
             (timestamp :startdate)
             (smallint :tz-offset)
             (refer-to :users false)
             (refer-to :protocols false))))
  (down [] (drop (table :experiments))))

(defmigration add-projects-experiments-table
  (up [] (create
           (tbl :projects_experiments
             (refer-to :experiments false)
             (refer-to :projects false))))
  (down [] (drop (table :projects_experiments))))

(defmigration add-keywords-table
  (up [] (create
           (tbl :keywords
             (char :tag)
             (refer-to :users false))))
  (down [] (drop (table :keywords)))
  )

(defmigration add-experiments-keywords-table
  (up [] (create
           (tbl :experiments_keywords
             (refer-to :experiments false)
             (refer-to :keywords false)
             (refer-to :users false)
             )))
  (down [] (drop (table :experiments_keywords))))

(defmigration add-projects-keywords-table
  (up [] (create
           (tbl :projects_keywords
             (refer-to :projects false)
             (refer-to :keywords false)
             (refer-to :users false)
             )))
  (down [] (drop (table :projects_keywords))))

(defmigration add-epochs-table
  (up [] (create
           (tbl :epochs
             (uuid :uuid)
             (timestamp :startdate)
             (smallint :end-tz-offset)
             (timestamp :enddate)
             (smallint :end-tz-offset)
             (refer-to :users false)
             (refer-to :experiments false))))
  (down [] (drop (table :epochs))))

(defmigration add-epochs-keywords-table
  (up [] (create
           (tbl :epochs_keywords
             (refer-to :epochs false)
             (refer-to :keywords false)
             (refer-to :users false)
             )))
  (down [] (drop (table :epochs_keywords))))

(defmigration add-properties-table
  (up [] (create
           (tbl :properties
             (char :key)
             (smallint :type)
             (double :double-value)
             (bigint :int-value)
             (clob :string-value)
             (refer-to :users false))))
  (down [] (drop (table :properties)))
  )

;(defmigration add-properties-index
;  (up [] (create
;           (index :properties :key)
;           (index :properties :int-value)))
;  (down [] (drop (index :properties :key)
;             (index :properties :int-value))))

(defmigration add-epochs-properties-table
  (up [] (create
           (tbl :epochs_properties
             (refer-to :epochs false)
             (refer-to :properties false)
             (refer-to :users false)
             )))
  (down [] (drop (table :epochs_properties))))
