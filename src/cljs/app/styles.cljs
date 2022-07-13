(ns app.styles
  (:require [cljss.core :refer-macros [defstyles]]))

;; TODO Remove unused
(def header-height 60)

(def colors
  {:primary "#fc3724"
   :text-default "#353b50"
   :text-light "#6d6d6d"
   :grey-light "#d1d1d1"
   :success "#009b00"
   :error "firebrick"})

(defstyles modal-overlay []
  {:background "rgba(0,0,0,0.2)"
   :position "fixed"
   :top 0
   :bottom 0
   :left 0
   :right 0})

(defstyles modal-content []
  {:margin "50px auto"
   :background-color "white"
   :padding "20px"
   :padding-bottom "40px"
   :border-radius "4px"
   :width "800px"
   :min-height "400px"
   :box-shadow "4px 4px 16px 4px rgba(0,0,0,0.2)"})

(defstyles circle-loader [variant]
  {:fill "transparent"
   :stroke (colors variant "#eee")
   :stroke-width "2"
   :animation "dash 2s ease infinite, rotate 2s linear infinite"})

(defstyles progress-button-content []
  {:display "flex"
   :align-items "center"
   :justify-content "center"})

(defstyles utility-page [full-width]
  {:width (if full-width "100%" "800px")
   :height "440px"})

(defstyles utility-page-content []
  {:display "flex"
   :height "100%"
   :align-items "center"
   :justify-content "center"})

(defstyles page-wrapper []
  {:margin-top (str (+ header-height 20) "px")
   :display "flex"
   :justify-content "center"
   :padding "0 10px"})

(defstyles page-title []
  {:display "flex"
   :align-items "center"
   :justify-content "space-between"
   :border-bottom (str "1px solid " (colors :grey-light))
   :margin-bottom "40px"})

(defstyles filter-label []
  {:color (:text-light colors)
   :font-size "12px"})

(defstyles search-header []
  {:height "80px"
   :paddin-bottom "20px"
   :display "flex"
   :align-items "center"
   :justify-content "space-between"})

(defstyles table-wrapper []
  {:flex 1
   "table" {:width "100%"}})

(defstyles table-sorting []
  {:display "flex"
   :gap "10px"
   :align-items "center"
   "label" {:margin "0"}
   "select" {:padding "2px 4px"
             :height "24px"}})

(defstyles table-info []
  {:display "flex"
   :align-items "center"
   :gap "20px"
   :font-size "12px"})

(defstyles form []
  {:display "flex"
   :flex-direction "column"
   :gap "20px"})

(defstyles form-actions []
  {:display "flex"
   :justify-content "space-between"
   :margin-top "20px"
   "button" {:width "150px"}})

(defstyles form-row []
  {:display "flex"
   :gap "10px"})

(defstyles form-group []
  {:width "100%"
   "> .invalid" {:border-color "red"}
   "*" {:width "100%"}})

(defstyles form-error []
  {:padding "6px 10px"
   :border (str "1px solid " (colors :error))
   :border-radius "4px"
   :color (colors :error)
   :text-align "center"
   })

(defstyles form-success []
  {:padding "6px 10px"
   :border (str "1px solid " (colors :success))
   :border-radius "4px"
   :color (colors :success)
   :text-align "center"
   })

(defstyles field-error []
  {:margin-top "6px"
   :font-size "12px"
   :color "red"})

(defstyles icon-button []
  {:padding "10px 0"
   :border "none"
   :opacity "0.5"
   :transition "opacity .15s ease-in-out"
   :&:hover {:opacity "1"}})

(def icons {:pencil "images/icon-pencil.png"
            :bin "images/icon-bin.png"})

(defstyles icon [icon]
  {:display "block"
   :width "20px"
   :height "20px"
   :background-image (str "url(\"" (icons icon) "\")")
   :background-repeat "no-repeat"})

(defstyles icon-delete []
  {:display "block"
   :width "20px"
   :height "20px"
   :background-image "url('images/icon-bin.png')"
   :background-repeat "no-repeat"})
