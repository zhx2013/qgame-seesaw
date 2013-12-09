(ns qgame-seesaw.autolayout
  (:gen-class :main true)
  (:use [seesaw.core]
        [seesaw.chooser]
        [seesaw.color]
        [seesaw.border]
        [qgame.api]))

(def area
  (text :multi-line? true
        :wrap-lines? true
        :font "MONOSPACED-PLAIN-14"
        :text ""
        :preferred-size [300 :by 700]))

;; The right-panel which is for outputs
(def area2
  (text :multi-line? true
        :wrap-lines? true
        :id :area2
        :text ""
        :font "MONOSPACED-PLAIN-14"
        :editable? false
        :background :black
        :foreground :white
        :preferred-size [300 :by 700]))


(def text-combo
  (grid-panel
    :border 10
    :items [area area2]
    :columns 2
    :hgap 15))


(def about-button
  (button :text "About"
          :listen [:action (fn [e] (alert "About \n Quantum Gate And Measurement Emulator \n 
			Original author: \n Lee Spector \n
			Clojure version authors: \n Omri Bernstein \n Evan Ricketts \n Haoxi Zhan \n Breton Handy \n Mitchel Fields"))]))

;; The "Save result" button
(def save-area2
  (button :text "Save result"
          :listen [:action (fn [e]
                             (spit (choose-file :type :save
                                                :multi? false)
                                   (text area2)))]))


(def clear-result
  (button :text "Clear result"
          :listen [:action (fn [e]
                             (text! area2 "Results"))]))


(def clear-entry
  (button :text "Clear input"
          :listen [:action (fn [e]
                             (text! area ""))]))

;; The "Save input" button
(def save-area
  (button :text "Save input"
          :listen [:action (fn [e]
                             (spit (choose-file :type :save
                                                :multi? false)
                                   (text area)))]))

;; The "Open" button
(def open-area
  (button :text "Open"
          :listen [:action (fn [e]
                             (text! area (slurp (choose-file :type :open :multi? false))))]))


(def top-bt-combo
  (grid-panel
    :items [open-area save-area clear-entry save-area2 clear-result about-button]
    :columns 6
    :rows 1
    :vgap 13
    :hgap 45))


(def field
  (text :multi-line? false
        :font "MONOSPACED-PLAIN-14"
        :columns 80
        :text ""))


(def qbt-combo
  (flow-panel
    :items [(label :text "Number of qubits: "
                   :font "MONOSPACED-PLAIN-14")
            field]
    :align :left
    :hgap 30))


(defn run-prog
  []
  (use 'qgame.api)
  (let [noq (try (Integer/parseInt (text field))
              (catch Exception e (alert "number-of-qubits is supposed to be an Integer.")))]
    (if (< noq 1)
      (alert "number-of-qubits too small")
      (->> (text area)
        read-string
        (execute-program {:num-qubits noq})
        list*
        str
        (text! area2)))))
        
        


(def b
  (button :text "Run code"
          :listen [:action (fn [e] (run-prog))]))

(def exit-b
  (button :text "Exit"
          :listen [:action (fn [e] (System/exit 0))]))


(def run-exit-combo
  (flow-panel
    :items [b exit-b]
    :align :left
    :hgap 265))


(def contents
  (vertical-panel
    :items [top-bt-combo text-combo qbt-combo run-exit-combo]))

(def f
  (frame :title "QGAME" 
         :minimum-size [900 :by 720]
         :content contents
         :on-close :exit))


(defn -main [& args]
  (native!)
  (-> f pack! show!))