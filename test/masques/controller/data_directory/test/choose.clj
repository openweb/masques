(ns masques.controller.data-directory.test.choose
  (:require [config.db-config :as db-config]
            [masques.model.system-properties :as system-properties])
  (:use clojure.test
        masques.controller.data-directory.choose)
  (:import [javax.swing ImageIcon]))

(defn no-op [])

(deftest test-show
  (let [old-saved-data-dir (system-properties/read-data-directory)
        old-data-dir (db-config/data-dir)
        new-data-dir "test_data_dir"]
    (system-properties/set-data-directory new-data-dir)
    (is (nil? (show no-op)))
    (system-properties/delete-data-directory)
    (let [frame (show no-op)]
      (is frame)
      ;(Thread/sleep 10000)
      (is (.isShowing frame))
      (click-save frame)
      (is (= (system-properties/read-data-directory) new-data-dir))
      (is (not (.isShowing frame)))
      (system-properties/set-data-directory old-saved-data-dir))))