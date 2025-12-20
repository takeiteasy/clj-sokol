(ns clj-sokol.core
  "Main entry point for clj-sokol.
   
   This namespace re-exports the main Sokol modules for convenience.
   
   Quick start:
   
   (require '[clj-sokol.core :as sokol])
   
   (sokol/run-app
     {:width 800
      :height 600
      :init #(sokol/setup-gfx)
      :frame #(do
                (sokol/begin-pass {})
                (sokol/end-pass)
                (sokol/commit))
      :cleanup #(sokol/shutdown-gfx)})"
  (:require [clj-sokol.gfx :as gfx]
            [clj-sokol.app :as app]
            [clj-sokol.time :as time]
            [clj-sokol.audio :as audio]))

;; =============================================================================
;; Graphics (re-exported from clj-sokol.gfx)
;; =============================================================================

(def setup-gfx gfx/setup)
(def shutdown-gfx gfx/shutdown)
(def begin-pass gfx/begin-pass)
(def end-pass gfx/end-pass)
(def commit gfx/commit)
(def apply-pipeline gfx/apply-pipeline)
(def apply-bindings gfx/apply-bindings)
(def draw gfx/draw)
(def color gfx/color)
(def bindings gfx/bindings)

;; =============================================================================
;; Application (re-exported from clj-sokol.app)
;; =============================================================================

(def run-app app/run)
(def width app/width)
(def height app/height)
(def request-quit app/request-quit)

;; =============================================================================
;; Time (re-exported from clj-sokol.time)
;; =============================================================================

(def setup-time time/setup)
(def now time/now)
(def elapsed-ms time/elapsed-ms)
(def elapsed-sec time/elapsed-sec)
(def make-frame-timer time/make-frame-timer)
(def frame-delta time/frame-delta)

;; =============================================================================
;; Audio (re-exported from clj-sokol.audio)
;; =============================================================================

(def setup-audio audio/setup)
(def shutdown-audio audio/shutdown)
(def expect-frames audio/expect)
(def push-samples audio/push)
