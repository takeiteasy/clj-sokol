(ns clj-sokol.time
  "Time measurement wrapper for sokol_time.
   
   Provides high-precision timing functions."
  (:import [org.sokol.wrapper Time]))

;; =============================================================================
;; Setup
;; =============================================================================

(defn setup
  "Initialize the timing system. Call once at startup."
  []
  (Time/setup))

;; =============================================================================
;; Time Measurement
;; =============================================================================

(defn now
  "Get the current time in ticks."
  []
  (Time/now))

(defn diff
  "Get the difference between two tick values."
  [new-ticks old-ticks]
  (Time/diff new-ticks old-ticks))

(defn since
  "Get ticks elapsed since a start time."
  [start-ticks]
  (Time/since start-ticks))

(defn laptime
  "Update a laptime value and return elapsed ticks.
   Pass an atom containing the last time value."
  [last-time-atom]
  (let [elapsed (since @last-time-atom)]
    (reset! last-time-atom (now))
    elapsed))

;; =============================================================================
;; Unit Conversions
;; =============================================================================

(defn ->sec
  "Convert ticks to seconds."
  [ticks]
  (Time/seconds ticks))

(defn ->ms
  "Convert ticks to milliseconds."
  [ticks]
  (Time/millis ticks))

(defn ->us
  "Convert ticks to microseconds."
  [ticks]
  (Time/micros ticks))

(defn ->ns
  "Convert ticks to nanoseconds."
  [ticks]
  (Time/nanos ticks))

;; =============================================================================
;; Convenience Functions
;; =============================================================================

(defn elapsed-ms
  "Get milliseconds elapsed since a start time."
  [start-ticks]
  (->ms (since start-ticks)))

(defn elapsed-sec
  "Get seconds elapsed since a start time."
  [start-ticks]
  (->sec (since start-ticks)))

(defmacro timing
  "Measure the execution time of body in milliseconds.
   Returns [result elapsed-ms].
   
   Usage:
   (let [[result ms] (timing (expensive-operation))]
     (println \"Took\" ms \"ms\"))"
  [& body]
  `(let [start# (now)
         result# (do ~@body)]
     [result# (elapsed-ms start#)]))

(defmacro with-timing
  "Execute body and print timing information.
   
   Usage:
   (with-timing \"Loading assets\"
     (load-all-assets))"
  [label & body]
  `(let [start# (now)
         result# (do ~@body)]
     (println ~label "took" (elapsed-ms start#) "ms")
     result#))

;; =============================================================================
;; Frame Timer
;; =============================================================================

(defn make-frame-timer
  "Create a frame timer for measuring delta time.
   Returns an atom that tracks the last frame time.
   
   Usage:
   (def timer (make-frame-timer))
   ;; In your frame loop:
   (let [dt (frame-delta timer)]
     (update-game dt))"
  []
  (atom (now)))

(defn frame-delta
  "Get the delta time since last frame in seconds.
   Updates the timer atom."
  [timer-atom]
  (let [elapsed (->sec (since @timer-atom))]
    (reset! timer-atom (now))
    elapsed))

(defn frame-delta-ms
  "Get the delta time since last frame in milliseconds.
   Updates the timer atom."
  [timer-atom]
  (let [elapsed (->ms (since @timer-atom))]
    (reset! timer-atom (now))
    elapsed))
