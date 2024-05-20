(ns books.isbn
  (:require [clj-http.client :as client]
            [cheshire.core :as ch]))

(def url "https://www.googleapis.com/books/v1/volumes?q=isbn:")

(defn fetch-book-data [isbn]
  (let [res (client/get (str url isbn))]
    (if (= (:status res) 200)
      (ch/parse-string (:body res))
      {})))

(defn transform-item [isbn item]
  (let [vol (item "volumeInfo")]
    (merge (select-keys vol
                        ["title"
                         "authors"
                         "publisher"
                         "language"
                         "description"
                         "categories"])
           {"isbn"  isbn
            "links" {"previewLink" (vol "previewLink")}})))

(defn transform-data [isbn data]
  (map #(transform-item isbn %)
       (data "items")))

(defn load-json [f]
  (ch/parse-string (slurp f)))

(defn save-json [f data]
  (spit f (ch/generate-string data {"pretty" true})))

(defn new-book [isbn f]
  (let [isbn-data (fetch-book-data isbn)
        db-data (load-json f)]
    (if (not (contains? (db-data "books") isbn))
      (let [book-data (first
                        (transform-data isbn isbn-data))
            new-data (merge (db-data "books")
                            {isbn book-data})]
        (when (not (nil? book-data))
          (save-json f {"books" new-data}))
        :added)
      :already-added)))

(comment
  "Examples of using the functions"

  (fetch-book-data "9780241953181")

  (->> "9780241953181"
       (fetch-book-data)
       (transform-data "9780241953181")
       (first))

  (->> "9789354898877"
       (fetch-book-data)
       (transform-data "9789354898877"))

  (load-json "db1.json")

  (save-json "db1.json" {"foo" 1})

  (new-book "9781405072960" "db1.json")

  )

