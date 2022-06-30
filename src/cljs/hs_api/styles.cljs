(ns hs-api.styles
  (:require [cljss.core :refer-macros [defstyles]]))

(def header-height 60)

(def colors
  {:primary "#fc3724"
   :text-default "#353b50"
   :text-light "#6d6d6d"
   :success "#009b00"
   :error "firebrick"})

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

(defstyles page-content []
  {:width "800px"
   :padding-top "20px"})

(defstyles app-title []
  {:margin "0"
   :padding "0"
   :margin-right "20px"})

(defstyles app-header []
  {:position "fixed"
   :top "0"
   :right "0"
   :left "0"
   :padding "0 20px"
   :height (str header-height "px")
   :display "flex"
   :align-items "center"
   :background-color "white"
   :border-bottom (str "2px " "solid" (:primary colors))})

(defstyles header-link []
  {:color (:text-default colors)
   :text-decoration "none"
   :font-weight 400
   :&:hover {:color (:primary colors)}})

(defstyles search-page []
  {:display "flex"
   :gap "20px"
   :width "100%"})

(defstyles filters []
  {:display "flex"
   :flex-direction "column"
   :gap "10px"
   :width "250px"
   "*" {:width "100%"}})

(defstyles filter-label []
  {:color (:text-light colors)
   :font-size "12px"})

(defstyles search-header []
  {:height "40px"
   :display "flex"
   :align-items "center"
   :justify-content "space-between"})

(defstyles table-wrapper []
  {:flex 1
   "table" {:width "100%"}})

(defstyles form []
  {:display "flex"
   :flex-direction "column"
   :gap "20px"})

(defstyles form-actions []
  {:display "flex"
   :justify-content "flex-end"
   :gap "10px"
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
