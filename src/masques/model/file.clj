(ns masques.model.file
  (:require [clojure.java.io :as io])
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update file record))

(defn by-album [album-id]
  (into [] (select file (where {:ALBUM_ID album-id}))))

(defn copy [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))

