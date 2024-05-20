(ns books.core
  (:gen-class)
  (:require [books.entry :as entr]))

(defn isbn-entry []
  (entr/ui))

(defn -main []
  (isbn-entry))

