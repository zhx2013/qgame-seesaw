(ns qgame-seesaw.core
        (:use [seesaw.core]))

(native!)
(def f (frame :title "QGAME" :width 800 :height 800))
(defn display [content]
        (config! f :content content)
        content)
(def area (text :multi-line? true :font "MONOSPACED-PLAIN-14"
        :text ""))
(def area2 (text :multi-line? true :font "MONOSPACED-PLAIN-14"
        :text (text area) :background :black :foreground :white))
(def b (button :text "Run code"))
(listen b :action (fn [e] (text! area2 (text area)))) ;this should update the text of lb
(defn display-split [left, right, bottom]
        (display (top-bottom-split
                (display (left-right-split (scrollable left) (scrollable right) :divider-location 1/2)) b :divider-location 8/9)))
(defn show-frame [] (-> f pack! show!) (display-split area area2 b))

(show-frame)
