(ns qgame-seesaw.core
  (:gen-class :main true)
  (:use [seesaw.core]))


(def f (frame :title "QGAME" :width 800 :height 800))
(defn display [content]
	(config! f :content content)
	content)
(def area (text :multi-line? true :font "MONOSPACED-PLAIN-14"
	:text ""))
(def area2 (text :multi-line? true :font "MONOSPACED-PLAIN-14"
	:text (text area) :background :black :foreground :white))
(def b (button :text "Run code"))
(defn display-split [left, right, bottom]
	(display (top-bottom-split
		(display (left-right-split (scrollable left) (scrollable right) :divider-location 1/2)) b :divider-location 8/9)))
(defn show-frame [] (-> f pack! show!) (display-split area area2 b))
(defn -main [& args]
	(native!)
	(listen b :action (fn [e] (text! area2 (text area))))
	(show-frame)
)
