(ns clj-sokol.examples.clear
  "Simple example that clears the screen to a cycling color.
   
   Run with: lein run -m clj-sokol.examples.clear"
  (:require [clj-sokol.app :as app]
            [clj-sokol.gfx :as gfx]
            [clj-sokol.time :as time])
  (:gen-class))

(def state (atom {:t 0.0}))

(defn init []
  (time/setup)
  (gfx/setup)
  (println "Initialized!"))

(defn frame []
  (let [{:keys [t]} @state
        ;; Cycle colors over time
        r (/ (+ 1.0 (Math/sin t)) 2.0)
        g (/ (+ 1.0 (Math/sin (+ t 2.094))) 2.0) ; +2π/3
        b (/ (+ 1.0 (Math/sin (+ t 4.188))) 2.0)] ; +4π/3
    
    (gfx/with-frame
      (gfx/with-pass {:clear-color [r g b 1.0]}
        ;; Nothing to draw, just clear
        ))
    
    ;; Update time
    (swap! state update :t + 0.02)))

(defn cleanup []
  (gfx/shutdown)
  (println "Cleaned up!"))

(defn -main [& args]
  (app/run
    {:width 800
     :height 600
     :title "clj-sokol Clear Example"
     :init init
     :frame frame
     :cleanup cleanup}))
