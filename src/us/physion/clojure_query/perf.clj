(ns us.physion.clojure-query.test.query
  (:use [us.physion.clojure-query.entities])
  (:require [us.physion.clojure-query.db :as db])
  (:require (korma [core :as korma] [db :as korma-db] ))
  (:require [korma.incubator.core :refer with])
  (:require [lobos.migrations :refer [migrate]]))

