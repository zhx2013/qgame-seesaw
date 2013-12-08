(ns qgame-seesaw.core
  (:gen-class :main true)
  (:use [seesaw.core]
	[seesaw.chooser]
	[qgame.core]
	[qgame.qgates]
	[qgame.pprint]))

;; The whole frame
(def f (frame :title "QGAME" :width 800 :height 800 :on-close :exit))

(defn display [content]
	"A function to display things on screen"
	(config! f :content content)
	content)

;; The left-panel which is for inputs
(def area (text :multi-line? true :font "MONOSPACED-PLAIN-14"
	:text ""))

;; The right-panel which is for outputs
(def area2 (text :multi-line? true :font "MONOSPACED-PLAIN-14"
	:text (text area) :background :black :foreground :white))

;; The "Exit button"
(def exit-b (button :text "Exit"
			:listen [:action (fn [e] (System/exit 0))]))

;; The "Number of qubits" inputing field
(def field (text :multi-line? false :font "MONOSPACED-PLAIN-14" :text""))

;; Running the program
(defn run-prog
	[]
	(let [noq (try (Integer/parseInt (text field)) (catch Exception e (alert "number-of-qubits is supposed to be an Integer.")))]
		(if (< noq 1)
			(alert "number-of-qubits too small")
			(text! area2 (str (execute-program {:num-qubits noq} (read-string (text area))))))))

;; The "About" button
(def about-button (button :text "About"
			:listen [:action (fn [e] (alert "About \n Quantum Gate And Measurement Emulator \n 
			Original author: \n Lee Spector \n
			Clojure version authors: \n Omri Bernstein \n Evan Ricketts \n Haoxi Zhan \n Breton Handy \n Mitchel Fields"))]))
			
;; The "Save result" button
(def save-area2 (button :text "Save result"
			:listen [:action (fn [e] (spit (choose-file :type :save :multi? false) (text area2)))]))
			
;; The "Save input" button
(def save-area (button :text "Save input"
			:listen [:action (fn [e] (spit (choose-file :type :save :multi? false) (text area)))]))
			
;; The "Open" button
(def open-area (button :text "Open"
			:listen [:action (fn [e] (text! area (slurp (choose-file :type :open :multi? false))))]))

;; The "Run code" button
(def b (button :text "Run code"
		:listen [:action (fn [e] (run-prog))]))
		
;; Splitting the whole frame
(defn display-split []
	(display (top-bottom-split
		;top buttons
		(display (left-right-split open-area 
			(display (left-right-split save-area 
				(display (left-right-split save-area2 about-button :divider-location 1/2))
				:divider-location 1/3))
			:divider-location 1/4))
		;lower things
		(display (top-bottom-split
			; The two panels of text areas
			(display (left-right-split (scrollable area) (scrollable area2) :divider-location 1/2))
			; The bottom commands (number-of-qubits and run/exit)
			(display (top-bottom-split
				;number-of-qubits
				(display (left-right-split "Number of qubits: " field))
				; run and exit buttons
				(display (left-right-split b exit-b :divider-location 1/2))
				; the divider-location of this bottom commands subframe (num-qubits and buttons)
				:divider-location 1/2))
			; the divider-location of the subframe except the top bar
			:divider-location 8/9))
		; the divider-location of top bar and other contents
		:divider-location 1/20)))
		
			
;; Showing the frames
(defn show-frame [] (-> f pack! show!) (display-split))

(defn -main [& args]
	(native!)
	(show-frame)
)
