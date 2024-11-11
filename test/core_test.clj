(ns core-test
  (:require [clojure.test :refer [is deftest testing]]
            [org.httpkit.client :as httpkit-client]
            [hato.client :as hato-client]
            [clj-http.client :as clj-http-client]))

(deftest endpoint1-post-test
  (testing "When posting has correct data form, returns 201 status (hato)")
  (testing "When posting has correct data form, returns 201 status (clj-http)")
  (testing "When posting has correct data form, returns 201 status (httpkit)"))