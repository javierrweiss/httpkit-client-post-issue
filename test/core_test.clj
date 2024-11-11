(ns core-test
  (:require [clojure.test :refer [is deftest testing run-test]]
            [org.httpkit.client :as httpkit-client]
            [hato.client :as hato-client]
            [clj-http.client :as clj-http-client]
            [jsonista.core :as j]))

(def url "http://localhost:2000/api/v1/endpoint1")

(def post-request {:a 4343
                   :b "This is a string"})
  
(deftest endpoint1-post-test
  (testing "When posting has correct data form, returns 201 status (hato)"
    (is (== 201 (-> (hato-client/post url {:body (j/write-value-as-string post-request)
                                           :content-type :json})
                    :status))))
  (testing "When posting has correct data form, returns 201 status (clj-http)"
    (is (== 201 (-> (clj-http-client/post url {:body (j/write-value-as-string post-request)
                                               :content-type :json})
                    :status))))
  (testing "When posting has correct data form, returns 201 status (httpkit)"
    (is (== 201 (-> @(httpkit-client/post url {:body (j/write-value-as-string post-request)
                                               :content-type :json})
                    :status)))))

(comment
  (run-test endpoint1-post-test)
  )