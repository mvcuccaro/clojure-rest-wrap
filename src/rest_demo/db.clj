(ns rest-demo.db
  (:require [clojure.java.jdbc :as sql]
            [dotenv :refer [env]]))

(def myenv (env))

;;define a db config map from .env file values
(def db-map
  (let [{dbuser "dbuser"
         dbpassword "dbpassword"
         db "db"
         host "dbhost"
         port "dbport"} myenv]

    {:subprotocol "mysql"
     :subname (str "//" host ":" port "/" db)
     :user dbuser
     :password dbpassword}))


(defn q
  "Get results of a query"
  [query]
  (sql/query db-map [query]))


(defn get-many
  "Build a get query"
  [table]
  (->> (str "SELECT * FROM " table) q)) ;;todo replace with query builder
