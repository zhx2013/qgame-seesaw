(ns qgame-seesaw.core
  (:gen-class :main true)
  (:use [seesaw.core]
        [seesaw.chooser]
        [seesaw.color]
        [seesaw.border]
        [qgame.api]))


(def text-area
  (grid-panel
    :border 10
    :columns 2
    :hgap 15
    :items [(text :multi-line? true
                  :wrap-lines? true
                  :id :area
                  :font "MONOSPACED-PLAIN-14"
                  :text "Wanings: \n 1. Outer parentheses are expected. \n 2. Quotation marks are not allowed. \n 3. Please clear this textarea before typing your program."
                  :preferred-size [300 :by 700]) 
            (scrollable (styled-text :wrap-lines? true
                                     :id :area2
                                     :text "Results"
                                     :font "MONOSPACED-PLAIN-14"
                                     :editable? false
                                     :background :black
                                     :foreground :white
                                     :preferred-size [300 :by 700]))]))


(def about-button
  (button :text "About"
          :listen [:action (fn [e] (alert "About \n Quantum Gate And Measurement Emulator \n 
			Original author: \n Lee Spector \n
			Clojure version authors: \n Omri Bernstein \n Evan Ricketts \n Haoxi Zhan \n Breton Handy \n Mitchel Fields"))]))


(def save-output
  (button :text "Save result"
          :listen [:action (fn [e]
                             (spit (choose-file :type :save
                                                :multi? false)
                                   (text (select text-area [:#area2]))))]))


(def clear-output
  (button :text "Clear result"
          :listen [:action (fn [e]
                             (text! (select text-area [:#area2]) "Results"))]))


(def clear-input
  (button :text "Clear input"
          :listen [:action (fn [e]
                             (text! (select text-area [:#area]) ""))]))


(def save-input-button
  (button :text "Save input"
          :listen [:action (fn [e]
                             (spit (choose-file :type :save
                                                :multi? false)
                                   (text (select text-area [:#area]))))]))


(def open-button
  (button :text "Open"
          :listen [:action (fn [e]
                             (text! (select text-area [:#area]) (slurp (choose-file :type :open :multi? false))))]))


(def top-buttons
  (grid-panel
    :columns 6
    :rows 1
    :vgap 13
    :hgap 45
    :items [open-button 
            save-input-button 
            clear-input 
            save-output 
            clear-output 
            about-button]))


(def qubit-input
  (flow-panel
    :align :left
    :hgap 30
    :items [(label :text "Number of qubits: "
                   :font "MONOSPACED-PLAIN-14")
            (text :multi-line? false
                  :id :field
                  :font "MONOSPACED-PLAIN-14"
                  :columns 10
                  :text "")]))


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
                            list*)
                original (text to)]
            (text! to (str original "\n\n" update))))))))
                

(def run-exit-buttons
  (flow-panel
    :align :left
    :hgap 265
    :items [(button :text "Run code"
                    :listen [:action (fn [e] (run-prog))]) 
            (button :text "Exit"
                    :listen [:action (fn [e] (System/exit 0))])]))


(def contents
  (vertical-panel
    :items [top-buttons text-area qubit-input run-exit-buttons]))

(def f
  (frame :title "QGAME" 
         :minimum-size [900 :by 720]
         :content contents
         :on-close :exit))


(defn -main [& args]
  (native!)
  (-> f pack! show!))
