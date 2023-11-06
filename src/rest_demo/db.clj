(ns rest-demo.db
  (:require [clojure.java.jdbc :as sql]
            [dotenv :refer [env app-env]]))

(def myenv (env))

(def db-map
  "Read .env file and return a db config map"
  (let [{dbuser "dbuser"
         dbpassword "dbpassword"
         db "db"
         host "dbhost"
         port "dbport"} myenv]

    {:subprotocol "mysql"
     :subname (str "//" host ":" port "/" db)
     :user dbuser
     :password dbpassword}))


(defn q [query]
  "Get results of a query"
  (sql/query db-map [query]))


(defn get-many [table]
  "Build a query"
  (->> (str "SELECT * FROM " table) q)) ;;todo replace with query builder
