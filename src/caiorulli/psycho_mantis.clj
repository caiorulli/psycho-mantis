(ns caiorulli.psycho-mantis
  (:require [clojure.walk :as w]
            [defntly.core :refer [defn-update-body]]))

(def ask "ask")

(defn- replace-ask
  [envsym body]
  (if (coll? body)
    (if (= (first body) 'ask)
      `(get ~envsym ~(second body))
      (w/walk (partial replace-ask envsym) identity body))
    body))

(defn- wrap-inject
  [_name body]
  (prn "before" body)
  (let [envsym (gensym "env")
        body'  (replace-ask envsym body)]
    (prn "after" body')
    `((fn [~envsym]
        ~@body'))))

(defmacro definject
  [& args]
  (defn-update-body wrap-inject args))
