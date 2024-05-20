(ns books.entry
  (:require [books.isbn :as isbn]
            [seesaw.core :as s]))

(def db-file (System/getenv "DB_PATH"))

(defn enter-data [isbn]
  (isbn/new-book isbn db-file))

(defn ui []
  (s/native!)
  (let [f (s/frame :title "ISBN Search" :size [280 :by 100])
        lbl (s/label "Enter ISBN")
        txt (s/text "")
        panel (s/vertical-panel :items [lbl txt])
        btn (s/button :text "Add")
        main-panel (s/border-panel
                     :north panel
                     :vgap 10
                     :hgap 10
                     :south btn)]
    (s/listen btn :action (fn [e]
                            (enter-data (clojure.string/replace (s/config txt
                                                                          :text)
                                                                "-" ""))
                            (s/request-focus! txt)))
    (s/config! f :content main-panel)
    (-> f s/show!)))


