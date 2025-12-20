# clj-sokol

> **WARNING** This is a work in progress

Very thin Clojure wrapper for [sokol](https://github.com/floooh/sokol) graphics libraries, built on top of [jsokol](https://github.com/takeiteasy/jsokol) Java bindings.

## Quick Start

```bash
git clone https://github.com/takeiteasy/jsokol.git
cd jsokol
make native # build native library
lein run
```
## Usage

### Basic Application

```clojure
(ns my-app.core
  (:require [clj-sokol.app :as app]
            [clj-sokol.gfx :as gfx]))

(defn -main []
  (app/run
    {:width 800
     :height 600
     :title "My Clojure App"
     
     :init 
     (fn []
       (gfx/setup))
     
     :frame 
     (fn []
       (gfx/with-frame
         (gfx/with-pass {:clear-color [0.2 0.2 0.2 1.0]}
           ;; Draw your scene here
           )))
     
     :cleanup 
     (fn []
       (gfx/shutdown))}))
```

### Graphics API

```clojure
(require '[clj-sokol.gfx :as gfx])

;; Initialize
(gfx/setup)

;; Create resources
(def vbuf (gfx/make-buffer {:data (float-array [...])}))
(def tex (gfx/make-image {:width 256 :height 256}))
(def view (gfx/make-view :texture tex))
(def sampler (gfx/make-sampler {}))

;; Create bindings
(def binds (gfx/bindings
             {:vertex-buffers [vbuf]
              :views [view]
              :samplers [sampler]}))

;; Render
(gfx/with-pass {:clear-color [0.1 0.1 0.1 1.0]}
  (gfx/apply-pipeline pipeline)
  (gfx/apply-bindings binds)
  (gfx/draw 0 3))
(gfx/commit)

;; Cleanup
(gfx/destroy-buffer vbuf)
(gfx/destroy-view view)
(gfx/destroy-sampler sampler)
(gfx/shutdown)
```

### Timing

```clojure
(require '[clj-sokol.time :as time])

(time/setup)

;; Measure elapsed time
(let [start (time/now)]
  (do-expensive-work)
  (println "Took" (time/elapsed-ms start) "ms"))

;; Frame timing
(def timer (time/make-frame-timer))

(defn update-game []
  (let [dt (time/frame-delta timer)]
    (update-physics dt)
    (update-animations dt)))

;; Convenience macro
(time/with-timing "Loading assets"
  (load-all-assets))
```

### Audio

```clojure
(require '[clj-sokol.audio :as audio])

;; Push mode
(audio/setup {:sample-rate 44100 :num-channels 2})

(loop []
  (let [frames (audio/expect)]
    (when (pos? frames)
      (let [samples (generate-samples frames)]
        (audio/push samples))))
  (recur))

(audio/shutdown)
```

## Namespaces

| Namespace | Description |
|-----------|-------------|
| `clj-sokol.core` | Main entry point, re-exports common functions |
| `clj-sokol.app` | Window management, input, lifecycle |
| `clj-sokol.gfx` | Graphics rendering, resources |
| `clj-sokol.time` | High-precision timing |
| `clj-sokol.audio` | Audio playback |

## License

```
Copyright (C) 2025 George Watson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
