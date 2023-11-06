(ns rest-demo.db
  (:require [clojure.java.jdbc :as sql]
            [dotenv :refer [env app-env]]))
 
 (println (env))

 (def myenv (env))

 (def db-map (let [{dbuser "dbuser" dbpassword "dbpassword" db "db"} myenv]
   (println dbpassword)
   {:subprotocol "mysql"
    :subname (str "//127.0.0.1:3306/" db)
    :user dbuser
    :password dbpassword}))

(defn q [query]
  (sql/query db-map [query]))
