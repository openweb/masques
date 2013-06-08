(ns config.test.db-config
  (:use clojure.test
        config.db-config))

(defn user-directory-fixture [function]
  (let [old-users-map (get-users-map)]
    (function)
    (reset-users-map old-users-map)))

(use-fixtures :each user-directory-fixture)

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
    (update-username-password "foo?" "bar")
    (is (= (user-data-directory) "data/db/foo_/"))
    (update-username-password "foo%" "bar")
    (is (= (user-data-directory) "data/db/foo_0/"))
    (update-data-directory old-data-dir)))