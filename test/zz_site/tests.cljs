(ns zz-site.tests
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [zz-site.util :as util]))


(deftest test-numbers
  (is (= 0 0)))

(deftest test-util-need-buttons-0
  (is (util/need-buttons-fn 11 40) [9 10 11 12 13 14]))

(deftest test-util-need-buttons-1
  (is (util/need-buttons-fn 1 4) [0 1 2]))

(deftest test-util-need-buttons-2
  (is (util/need-buttons-fn 1 10) [0 1 2 3 4 5]))

(deftest test-util-need-buttons-3
  (is (util/need-buttons-fn 2 4) [0 1 2]))

(deftest test-util-need-buttons-4
  (is (util/need-buttons-fn 5 7) [1 2 3 4 5]))
