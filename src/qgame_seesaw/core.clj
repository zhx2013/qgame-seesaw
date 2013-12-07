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
(def exit-b (button :text "Exit"))
(defn display-split []
	(display (top-bottom-split
		(display (left-right-split (scrollable area) (scrollable area2) :divider-location 1/2)) 
		(display (left-right-split b exit-b :divider-location 1/2)) :divider-location 8/9)))
(defn show-frame [] (-> f pack! show!) (display-split))
(defn -main [& args]
	(native!)
	(listen b :action (fn [e] (text! area2 (text area))))
	(listen exit-b :action (fn [e] (System/exit 0)))
	(show-frame)
)
