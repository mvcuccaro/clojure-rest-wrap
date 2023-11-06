(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            ;[clojure.pprint :as pp]
            ;[clojure.string :as str]
            ;[clojure.data.json :as json]
            [rest-demo.db :as db])
  (:gen-class))

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
  (let [ret {:headers {"Content-Type" "text/html"}}]
    (try
      (let [data (->> req
                      (:params)
                      (:table)
                      (db/get-many))]
        (merge ret {:status 200 :body data}))
      (catch Exception e (merge ret {:status 500 :body (.getMessage e)})))))

(defroutes app-routes
  (GET "/" [] simple-body-page)
  ;(GET "/request" [] request-example)
  (GET "/api/:table" [] generic-query) ;;todo - handle query failure
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
