(ns clj-sokol.examples.triangle
  "Triangle example using the high-level wrapper API.
   
   This is a Clojure port of WrapperTriangle.java.
   
   Run with: lein run -m clj-sokol.examples.triangle"
  (:require [clj-sokol.app :as app]
            [clj-sokol.gfx :as gfx])
  (:import [org.sokol.wrapper Gfx Buffer Shader Pipeline Bindings PassAction Color VertexFormat]
           [org.sokol.examples TriangleSappShader]
           [java.lang.foreign Arena])
  (:gen-class))

;; ============================================================================
;; Application State
;; ============================================================================

(def state (atom {:vertex-buffer nil
                  :pipeline nil}))

;; ============================================================================
;; Callbacks
;; ============================================================================

(defn init []
  (gfx/setup)
  
  ;; Create vertex buffer with colored triangle
  ;; Format: x, y, z, r, g, b, a
  (let [vertices (float-array
                   [;; positions            colors
                     0.0   0.5  0.5         1.0 0.0 0.0 1.0    ; top: red
                     0.5  -0.5  0.5         0.0 1.0 0.0 1.0    ; right: green
                    -0.5  -0.5  0.5         0.0 0.0 1.0 1.0])  ; left: blue
        vbuf (Buffer/vertex vertices)]
    
    ;; Create shader and pipeline using TriangleSappShader from Java
    (with-open [arena (Arena/ofConfined)]
      (let [shader-desc (TriangleSappShader/triangleShaderDesc 
                          (.value (Gfx/backend)) 
                          arena)
            shader (Shader/create shader-desc)
            pipeline (-> (Pipeline/builder)
                         (.shader shader)
                         (.layout (into-array VertexFormat
                                    [VertexFormat/FLOAT3 VertexFormat/FLOAT4]))
                         (.build))]
        
        (reset! state {:vertex-buffer vbuf
                       :pipeline pipeline}))))
  
  (println "Init complete! Backend:" (Gfx/backend)))

(defn frame []
  (let [{:keys [vertex-buffer pipeline]} @state]
    (gfx/begin-pass {:clear-color [0.0 0.0 0.0 1.0]})
    (gfx/apply-pipeline pipeline)
    (gfx/apply-bindings (Bindings/vertex vertex-buffer))
    (gfx/draw 0 3)
    (gfx/end-pass)
    (gfx/commit)))

(defn cleanup []
  (gfx/shutdown)
  (println "Cleanup complete!"))

;; ============================================================================
;; Main
;; ============================================================================

(defn -main [& args]
  (app/run
    {:width 640
     :height 480
     :title "Clojure Wrapper Triangle"
     :init init
     :frame frame
     :cleanup cleanup}))
