(ns caiorulli.psycho-mantis
  (:require [clojure.walk :as w]
            [defntly.core :refer [defn-update-body]]))

(def ask "ask")
(def inject "inject")

(defn- replace-lexers
  [envsym body]
  (if (coll? body)
    (cond
      (= (first body) 'ask)
      `(get ~envsym ~(second body))

      (= (first body) 'inject)
      `(~(second body) ~envsym)

      :else
      (w/walk (partial replace-lexers envsym) identity body))
    body))

(defn- wrap-inject
  [_name body]
  (let [envsym (gensym "env")
        body'  (replace-lexers envsym body)]
    `((fn [~envsym]
        ~@body'))))

(defmacro definject
  [& args]
  (defn-update-body wrap-inject args))
