(ns haikus.db-test
  (:require [clojure.test :refer :all]
            [haikus.db :refer :all]))

(deftest cmu-syllable-count-test
  (testing "A single syllable"
    (is (= 1 (cmu-syllable-count "AH0"))))
  (testing "3 syllable string"
    (is (= 3 (cmu-syllable-count "AH0 B R EY1 ZH AH0 N Z")))))

