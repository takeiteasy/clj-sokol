(ns clj-sokol.core-test
  (:require [clojure.test :refer :all]
            [clj-sokol.time :as time]))

(deftest time-setup-test
  (testing "Time setup and basic operations"
    (time/setup)
    (is (pos? (time/now)))
    (let [start (time/now)
          _ (Thread/sleep 10)
          elapsed (time/elapsed-ms start)]
      (is (>= elapsed 9.0) "Should have elapsed at least 9ms"))))

(deftest time-conversions-test
  (testing "Time unit conversions"
    (time/setup)
    (let [ticks 1000000000] ; 1 billion ticks
      ;; Just verify conversions don't throw
      (is (number? (time/->sec ticks)))
      (is (number? (time/->ms ticks)))
      (is (number? (time/->us ticks)))
      (is (number? (time/->ns ticks))))))

(deftest frame-timer-test
  (testing "Frame timer"
    (time/setup)
    (let [timer (time/make-frame-timer)
          _ (Thread/sleep 10)
          dt (time/frame-delta-ms timer)]
      (is (>= dt 9.0) "Delta should be at least 9ms"))))
