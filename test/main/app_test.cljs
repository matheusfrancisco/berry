(ns main.app-test
  (:require [clojure.test :refer [deftest is]]))

(deftest dummy-test
  (is (= {:value 1}
         {:value 1})))
