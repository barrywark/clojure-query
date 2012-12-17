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
  (down [] (drop (table :projects))))
