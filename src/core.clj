(ns core
  (:require [org.httpkit.server :refer [run-server]]
            [muuntaja.core :as m]
            [reitit.ring :as ring] 
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.coercion :as coercion]
            [malli.util :as mu]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            [reitit.ring.spec :as spec]
            [reitit.swagger    :as api-docs]
            [reitit.swagger-ui :as api-docs-ui]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja   :as middleware-muuntaja]
            [malli.core :as malli]
            [reitit.ring.middleware.exception :as exception]))

(defn post-handler
  [_]
  {:status 201})

(def app 
  (ring/ring-handler
   (ring/router
    [["/swagger.json"
      {:get {:no-doc  true
             :swagger {:info {:title "HTTPKIT POST ISSUE" 
                              :version "0.1.0"}}
             :handler (api-docs/create-swagger-handler)}}]
     ["/api"
      ["/v1"
       ["/endpoint1"
        {:post {:parameters {:body [:map 
                                    [:a int?]
                                    [:b string?]]}
                :handler post-handler}}]]]]
    {:data {:coercion (reitit.coercion.malli/create
                       {:error-keys #{:type :coercion :in :schema :value :errors :humanized :transformed} 
                        :compile mu/closed-schema 
                        :strip-extra-keys true 
                        :default-values true 
                        :options nil})
            :muuntaja   m/instance
            :middleware [api-docs/swagger-feature 
                         parameters/parameters-middleware 
                         middleware-muuntaja/format-negotiate-middleware
                         middleware-muuntaja/format-response-middleware  
                         exception/exception-middleware 
                         middleware-muuntaja/format-request-middleware
                         coercion/coerce-response-middleware 
                         coercion/coerce-request-middleware
                         coercion/coerce-exceptions-middleware]} 
     :exception pretty/exception
     :validate spec/validate})
     (ring/routes 
      (api-docs-ui/create-swagger-ui-handler {:path "/"})
      (ring/create-default-handler))))

(defonce server (atom nil))

(defn start
  []
  (->> (run-server #'app {:port 2000 :join? false})
      (reset! server)))

(defn stop
  []
  (when @server
    (@server :timeout 100)
    (reset! server nil)))

(comment 
  (require '[jsonista.core :as j]
           '[malli.core :as malli])

  (start)

  (stop)
  

  (malli/validate [:map
                   [:a int?]
                   [:b string?]] 
                  {:a 1 :b "sdadss"})
  
  (app {:request-method :post
        :content-type "application/json"
        :uri "/api/v1/endpoint1"
        :body (j/write-value-as-bytes {:a 1 :b "sdadss"})}) 

    
  )