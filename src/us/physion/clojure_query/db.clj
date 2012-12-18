(ns us.physion.clojure-query.db
  (:require [korma [db :as korma]])
  (:import org.h2.tools.Server))

(def testdbspec {:classname   "org.h2.Driver"
                 :subprotocol "h2"
                 :subname     "~/test;AUTO_SERVER=TRUE"
                 :user        "testuser"
                 :password    "testpass"})

(korma/defdb testdb testdbspec)

(defn start-server []
  (let [server (.. Server (createTcpServer (into-array String ["-tcpAllowOthers"])))]
    (when (not (. server isRunning false))
      ;; Start Server
      (. server start))))


(defn stop-server []
  (let [server (.. Server (createTcpServer (into-array String ["-tcpAllowOthers"])))]
    (when (. server isRunning false)
      (. server stop))))
