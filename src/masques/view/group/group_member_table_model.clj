(ns masques.view.group.group-member-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as model-base]
            [masques.model.grouping-profile :as grouping-profile-model]
            [masques.model.profile :as profile-model]
            [masques.view.utils :as utils]
            [masques.view.utils.button-table :as button-table])
  (:use [masques.view.utils.korma-table-model :exclude [create]]))

(def date-added-column-id :date-added)
(def name-column-id :name)
(def remove-column-id :remove)
(def view-column-id :view)

(defn id-value-at
  "A value at function which returns the id of the grouping-profile for the
given group at the given row index."
  [group-id row-index column-id]
  (model-base/id
    (grouping-profile-model/find-table-grouping-profile group-id row-index)))

(def columns [{ id-key name-column-id
                text-key (term/name)
                class-key String
                value-at-key
                  (fn [group-id row-index column-id]
                    (profile-model/alias
                      (grouping-profile-model/find-profile
                        (grouping-profile-model/find-table-grouping-profile
                          group-id row-index))))}
              { id-key date-added-column-id
                text-key (term/date-added)
                class-key String
                value-at-key
                  (fn [group-id row-index column-id]
                    (str
                      (model-base/find-created-at
                        (grouping-profile-model/find-table-grouping-profile
                          group-id row-index)))) }
              (button-table/create-table-button-column-map
                view-column-id id-value-at)
              (button-table/create-table-button-column-map
                remove-column-id id-value-at)])

(deftype GroupMemberTableModel [group-id column-map]

  TableDbModel
  (db-entity [this]
    model-base/grouping-profile)
  
  (row-count [this]
    (grouping-profile-model/count-grouping-profiles group-id))
  
  (value-at [this row-index column-id]
    ((find-value-at-fn column-map column-id)
      group-id row-index column-id))
  
  (update-value [this row-index column-id value]
    ((find-update-value-fn column-map column-id)
      group-id row-index column-id value))
  
  (index-of [this record-or-id]
    (grouping-profile-model/table-index-of record-or-id group-id)))

(defn create
  "Creates a new group member table model which shows all of the group members
of the group with the given id."
  [group-id]
  (create-from-columns
    columns
    (GroupMemberTableModel.
      (model-base/id group-id) (create-column-map columns))))

(defn view-button-cell-renderer
  "A table cell renderer function for the view button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-under-construction-link-button :text (term/view))]
    (utils/save-component-property button id-key value)
    button))

(defn remove-button-cell-renderer
  "A table cell renderer function for the view button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-link-button :text (term/remove))]
    (utils/save-component-property button id-key value)
    button))