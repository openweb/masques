(ns config.test.db-config
  (:require [clojure.java.io :as io])
  (:use clojure.test
        config.db-config))

(defn user-directory-fixture [function]
  (let [old-users-map (ensure-users-map)]
    (function)
    (reset-users-map old-users-map)))

(use-fixtures :each user-directory-fixture)

(defn delete-directory
  "Deletes the given directory"
  [directory]
  (when (.isDirectory directory)
    (doseq [child (.listFiles directory)]
      (delete-directory child)))
  (io/delete-file directory))

(deftest test-user-directory-taken?
  (is (not (user-directory-taken? "blah" "blah")))
  (add-username "blah")
  (is (not (user-directory-taken? "blah" "blah")))
  (add-username "blah?")
  (is (user-directory-taken? "blah%" "blah_")))

(deftest test-user-directory
  (is (= (user-directory "foo") "foo"))
  (is (= (user-directory "foo?") "foo_"))
  (is (= (user-directory "foo bar") "foo_bar"))
  (add-username "foo?")
  (is (= (user-directory "foo?") "foo_"))
  (is (= (user-directory "foo%") "foo_0"))
  (add-username "foo%")
  (is (= (user-directory "foo%") "foo_0"))
  (is (= (user-directory "foo:") "foo_1")))

(deftest test-user-data-directory
  (let [old-data-dir (data-dir)]
    (update-data-directory "data/db/")
    (update-username-password "foo" "bar")
    (is (= (user-data-directory) "data/db/foo/"))
    (delete-directory (io/file (user-data-directory)))
    (update-username-password "foo?" "bar")
    (is (= (user-data-directory) "data/db/foo_/"))
    (delete-directory (io/file (user-data-directory)))
    (update-username-password "foo%" "bar")
    (is (= (user-data-directory) "data/db/foo_0/"))
    (delete-directory (io/file (user-data-directory)))
    (let [test-username "foofoo"]
      (add-username test-username)
      (is (= (user-data-directory test-username)
             (str "data/db/" test-username "/"))))
    (update-data-directory old-data-dir)))

(deftest test-username-file
  (let [old-data-dir (data-dir)]
    (update-data-directory "data/db/")
    (update-username-password "foo" "bar")
    (is (= (username-file) "data/db/foo/username.clj"))
    (let [test-username "foofoo"]
      (add-username test-username)
      (is (= (username-file test-username) (str "data/db/" test-username "/username.clj"))))
    (delete-directory (io/file (user-data-directory)))
    (update-data-directory old-data-dir)))

(deftest test-ensure-users-map
  (let [old-data-dir (data-dir)
        test-username "foo"]
    (update-data-directory "data/db/")
    (update-username-password test-username "bar")
    (reset-users-map)
    (ensure-users-map)
    (is (user-exists? test-username))
    (is (is (find-user-directory test-username) "foo"))
    (delete-directory (io/file (user-data-directory)))
    (update-data-directory old-data-dir)))