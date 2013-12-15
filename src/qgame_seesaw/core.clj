(ns qgame-seesaw.core
  (:gen-class :main true)
  (:use [seesaw.core]
        [seesaw.chooser]
        [seesaw.color]
        [seesaw.border]
        	[qgame.api])
  (:require [clojure.java.browse]))


(def text-area
  (grid-panel
    :border 10
    :columns 2
    :hgap 15
    :items [(scrollable (styled-text
                  :wrap-lines? true
                  :id :area
                  :font "MONOSPACED-PLAIN-14"
                  :text "Warnings: \n 1. Outer parentheses are expected. \n 2. Quotation marks are not allowed. \n 3. Please clear this text area before typing your program."
                  :preferred-size [300 :by 700])) 
            (scrollable (styled-text :wrap-lines? true
                                     :id :area2
                                     :text "Results"
                                     :font "MONOSPACED-PLAIN-14"
                                     :editable? false
                                     :background :black
                                     :foreground :white
                                     :preferred-size [300 :by 700]))]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;    file menu      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def about-bar
  (menu-item :text "About"
             :listen [:action (fn [e] (alert 
                                        (str "About Schrödinbug \n Quantum Gate And Measurement Emulator \n Version: RC6\n\n" 
                                              "Original author: \n Lee Spector \n\n"
                                              "Clojure version authors: \n Omri Bernstein \n Evan Ricketts \n Haoxi Zhan \n Breton Handy \n Mitchel Fields")))]))

(def help-bar
	(menu-item :text "Help"
            		:listen [:action (fn [e]
                                 (clojure.java.browse/browse-url "http://gibson.hampshire.edu/~qgame/"))]))

(def save-input-bar
  (menu-item :text "Save input"
          :listen [:action (fn [e]
                             (spit (choose-file :type :save
                                                :multi? false)
                                   (text (select text-area [:#area]))))]))

(def save-output-bar
  (menu-item :text "Save results"
          :listen [:action (fn [e]
                             (spit (choose-file :type :save
                                                :multi? false)
                                   (text (select text-area [:#area2]))))]))

(def open-bar
  (menu-item :text "Open"
             :listen [:action (fn [e]
                                (text! (select text-area [:#area]) (slurp (choose-file :type :open :multi? false))))]))

(defn quit-egg
	[]
	(let [x (rand-int 20)]
		(if (< x 10)
			(alert "You are caught by quantum crabs. \nThus you are not allowed to exit.")
			(System/exit 0))))

(def quit-bar 
  (menu-item :text "Quit"
             :listen [:action (fn [e] (quit-egg))]))

(def bar
  (menubar 
    :items [(menu
              :text "File"
              :items [open-bar
                      save-input-bar
                      save-output-bar
                      about-bar 
                      help-bar
                      quit-bar])]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;    clear buttons      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def clear-output
  (button :text "Clear results"
          :listen [:action (fn [e]
                             (text! (select text-area [:#area2]) "Results"))]))


(def clear-input
  (button :text "Clear input"
          :listen [:action (fn [e]
                             (text! (select text-area [:#area]) ""))]))

(def top-buttons
  (grid-panel
    :columns 6
    :rows 1
    :vgap 13
    :hgap 45
    :items [clear-input  
            clear-output])) 




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;    qubit input/run      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def qubit-input
  (flow-panel
    :align :left
    :hgap 30
    :items [(label :text "Number of qubits: "
                   :font "MONOSPACED-PLAIN-14")
            (text :multi-line? false
                  :font "MONOSPACED-PLAIN-14"
                  :id :field
                  :columns 10
                  :text "")
            (flow-panel
              :align :left
              :items [(button :text "Run code"
                              :id :run-code)])]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;    program runner      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn run-prog
  []
  (use 'qgame.api)
  (let [noq (try (Integer/parseInt (text (select qubit-input [:#field])))
                 (catch Exception e (alert "number-of-qubits is supposed to be an Integer.")))]
    (if (< noq 1)
      (alert "number-of-qubits too small")
      (let [read-string (read-string (text (select text-area [:#area])))]
        (if (not (coll? (first read-string)))
          (alert "Your input seems to be invalid... \n\nInput Guidelines: \n 1. Outer parentheses are expected. \n 2. Quotation marks are not allowed.")
          (let [to (select text-area [:#area2])
                update (->> read-string
                            (execute-program {:num-qubits noq})
                            list*
                            all-to-floats
                            (map all-to-cstrings))
                original (text to)]
            (text! to (str original "\n\n" (list* update)))))))))

(listen (select qubit-input [:#run-code]) :action (fn [e] (run-prog)))          

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;    put everything together      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def contents
  (vertical-panel
    :items [top-buttons text-area qubit-input]))

(def f
  (frame :title "Schrödinbug" 
         :minimum-size [900 :by 720]
         :content contents
         :menubar bar
         :on-close :exit))


(defn -main [& args]
  (native!)
  (-> f pack! show!))
