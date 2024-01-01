(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [dotenv :refer [env]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            ;[clojure.pprint :as pp]
            ;[clojure.string :as str]
            [clojure.data.json :as json]
            [rest-demo.db :as db]
            [clj-http.client :as client])
  (:gen-class))

(def myenv (env))

(def last-results (atom []))


; Simple Body Page
(defn simple-body-page []
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Api Running"})

; request-example
;; (defn request-example [req] 
;;      {:status  200
;;       :headers {"Content-Type" "text/html"}
;;       :body    (->>
;;                 (pp/pprint req)
;;                 (str "Request Object: " req))})

(defn generic-query
  "Run Query and send back results or send a 500 with error message"
  [req]
  (future
    (println (str (.getName (Thread/currentThread)) "\n"))
    (let [ret {:headers {"Content-Type" "text/"}}]
      (try
        (let [data (->> req
                        (:params)
                        (:table)
                        (db/get-many))]
          (reset! last-results data)
          (merge ret {:status 200 :body (json/write-str data)}))
        (catch Exception e
          (merge ret {:status 500 :body "Query Failed"}))))))


(defn get-docker-repositories
  "get docker repositories from provided registry"
  [_]
  (let [{docker-url "dockerurl"
         docker-user "dockeruser"
         docker-password "dockerpassword"} myenv]
    (:body
     (client/get
      docker-url
      {:basic-auth [docker-user docker-password]
       :accept :json}))))

(defn get-github-repositories
  "get github repositories from github url"
  [_]
  (let [{github-url "githubreposurl"} myenv]
    (println github-url)
    (:body
     (->>
      github-url
      (client/get)))))


(defroutes app-routes
  (GET "/" [] simple-body-page)
  ;(GET "/request" [] request-example)
  (GET "/api/docker_repositories" [] get-docker-repositories) ;;todo - handle query failure
  (GET "/api/github_repositories" [] get-github-repositories)
  (GET "/api/:table" [] generic-query)
  (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3001"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
