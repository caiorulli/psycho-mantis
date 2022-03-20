(ns caiorulli.psycho-mantis-test
  (:require [clojure.test :refer [deftest testing is]]
            [caiorulli.psycho-mantis :refer [definject ask inject]]))

(deftest definject-test
  (testing "works like defn without dependencies"
    (definject plus
      [a b]
      (+ a b))

    (is (= 42 ((plus 40 2) nil))))

  (testing "fetches deps by key"
    (definject plus-base
      [a b]
      (+ a b (ask :base)))

    (is (= 42 ((plus-base 20 20) {:base 2})))

    (definject sum-base
      []
      (reduce + 0 [20 20 (ask :base)]))

    (is (= 42 ((sum-base) {:base 2}))))

  (testing "is able to use other injectable functions seamlessly"
    (definject with-emphasis
      [s]
      (str s (ask :emphasis)))

    (definject with-prefix-and-emphasis
      [s]
      (str (ask :prefix) (inject (with-emphasis s))))

    (is (= "Hello, world!"
           ((with-prefix-and-emphasis "world") {:emphasis "!"
                                                :prefix   "Hello, "})))))
