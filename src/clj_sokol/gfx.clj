(ns clj-sokol.gfx
  "Graphics API wrapper for sokol_gfx.
   
   Provides functions for graphics initialization, resource management,
   and rendering operations."
  (:import [org.sokol.wrapper Gfx Buffer Image Sampler Shader Pipeline View Bindings Bindings$Builder PassAction Color]))

;; =============================================================================
;; Lifecycle
;; =============================================================================

(defn setup
  "Initialize the graphics system. Call from your init callback."
  []
  (Gfx/setup))

(defn shutdown
  "Shut down the graphics system. Call from your cleanup callback."
  []
  (Gfx/shutdown))

(defn valid?
  "Check if the graphics system is valid."
  []
  (Gfx/isValid))

;; =============================================================================
;; Color Helpers
;; =============================================================================

(defn color
  "Create a Color from RGBA values (0.0 to 1.0).
   
   Usage:
   (color 1.0 0.0 0.0 1.0) ; red
   (color 0.5 0.5 0.5)     ; gray with alpha=1"
  ([r g b]
   (Color. (float r) (float g) (float b)))
  ([r g b a]
   (Color. (float r) (float g) (float b) (float a))))

(def black Color/BLACK)
(def white Color/WHITE)
(def red Color/RED)
(def green Color/GREEN)
(def blue Color/BLUE)
(def yellow Color/YELLOW)
(def cyan Color/CYAN)
(def magenta Color/MAGENTA)
(def transparent Color/TRANSPARENT)

;; Cornflower blue - common clear color
(def cornflower-blue (color 0.392 0.584 0.929 1.0))

;; =============================================================================
;; Rendering
;; =============================================================================

(defn begin-pass
  "Begin a render pass.
   
   Options:
   - :clear-color [r g b a] - clear color as vec (default: cornflower blue)
   - :clear-depth float - depth clear value (default: 1.0)
   - :action PassAction - custom pass action (overrides other options)
   
   Examples:
   (begin-pass {})
   (begin-pass {:clear-color [0.1 0.1 0.1 1.0]})
   (begin-pass {:action (PassAction/load)})"
  ([]
   (begin-pass {}))
  ([opts]
   (let [action (or (:action opts)
                    (PassAction/clear 
                      (if-let [[r g b a] (:clear-color opts)]
                        (color r g b a)
                        cornflower-blue)))]
     (Gfx/beginPass action))))

(defn end-pass
  "End the current render pass."
  []
  (Gfx/endPass))

(defn commit
  "Commit the frame."
  []
  (Gfx/commit))

(defn apply-pipeline
  "Apply a pipeline for rendering."
  [pipeline]
  (Gfx/applyPipeline pipeline))

(defn apply-bindings
  "Apply resource bindings for rendering."
  [bindings]
  (Gfx/applyBindings bindings))

(defn draw
  "Issue a draw call.
   
   Arguments:
   - base-element: First element to draw (usually 0)
   - num-elements: Number of elements (vertices or indices) to draw
   - num-instances: (optional) Number of instances (default: 1)"
  ([base-element num-elements]
   (Gfx/draw base-element num-elements))
  ([base-element num-elements num-instances]
   (Gfx/draw base-element num-elements num-instances)))

;; =============================================================================
;; Resource Creation
;; =============================================================================

(defn make-vertex-buffer
  "Create a vertex buffer from float data.
   
   Usage:
   (make-vertex-buffer (float-array [0.0 0.5 0.0  -0.5 -0.5 0.0  0.5 -0.5 0.0]))"
  [^floats data]
  (Buffer/vertex data))

(defn make-index-buffer
  "Create an index buffer from short or int data.
   
   Usage:
   (make-index-buffer (short-array [0 1 2]))
   (make-index-buffer (int-array [0 1 2]))"
  [data]
  (if (instance? (Class/forName "[S") data)
    (Buffer/index ^shorts data)
    (Buffer/index ^ints data)))

(defn make-image
  "Create an image (texture) using the builder pattern.
   
   Options:
   - :width - image width (default: 1)
   - :height - image height (default: 1)
   - :data - byte array of pixel data (optional)
   - :label - debug label (optional)
   
   Usage:
   (make-image {:width 256 :height 256})"
  [{:keys [width height data label]
    :or {width 1 height 1}}]
  (let [builder (-> (Image/builder)
                    (.size (int width) (int height)))]
    (when data
      (.data builder (byte-array data)))
    (when label
      (.label builder label))
    (.build builder)))

(defn make-sampler
  "Create a texture sampler.
   
   Options:
   - :min-filter - :nearest or :linear (default: :linear)
   - :mag-filter - :nearest or :linear (default: :linear)
   
   Usage:
   (make-sampler {})                           ; default linear filtering
   (make-sampler {:min-filter :nearest})       ; pixelated look"
  ([]
   (Sampler/createDefault))
  ([{:keys [min-filter mag-filter] :as opts}]
   (if (and (nil? min-filter) (nil? mag-filter))
     (Sampler/createDefault)
     (if (= :nearest (or min-filter mag-filter))
       (Sampler/createNearest)
       (Sampler/createDefault)))))

(defn make-view
  "Create a view for an image or buffer.
   
   Usage:
   (make-view :texture image)
   (make-view :storage-buffer buffer)
   (make-view :color-attachment image)
   (make-view :depth-stencil image)"
  [type resource]
  (case type
    :texture (View/forTexture resource)
    :storage-buffer (View/forStorageBuffer resource)
    :color-attachment (View/forColorAttachment resource)
    :depth-stencil (View/forDepthStencilAttachment resource)))

;; =============================================================================
;; Bindings Builder
;; =============================================================================

(defn bindings
  "Create resource bindings for a draw call.
   
   Options:
   - :vertex-buffers - vector of vertex buffers
   - :index-buffer - index buffer
   - :views - vector of views (textures)
   - :samplers - vector of samplers
   
   Usage:
   (bindings {:vertex-buffers [vbuf]})
   (bindings {:vertex-buffers [vbuf]
              :views [tex-view]
              :samplers [sampler]})"
  [{:keys [vertex-buffers index-buffer views samplers]}]
  (let [builder (Bindings/builder)]
    (doseq [vb vertex-buffers]
      (.vertexBuffer builder vb))
    (when index-buffer
      (.indexBuffer builder index-buffer))
    (doseq [v views]
      (.view builder v))
    (doseq [s samplers]
      (.sampler builder s))
    (.build builder)))

;; =============================================================================
;; Resource Destruction
;; =============================================================================

(defn destroy-buffer [buffer] (Gfx/destroyBuffer buffer))
(defn destroy-image [image] (Gfx/destroyImage image))
(defn destroy-sampler [sampler] (Gfx/destroySampler sampler))
(defn destroy-shader [shader] (Gfx/destroyShader shader))
(defn destroy-pipeline [pipeline] (Gfx/destroyPipeline pipeline))
(defn destroy-view [view] (Gfx/destroyView view))

;; =============================================================================
;; Convenience Macros
;; =============================================================================

(defmacro with-pass
  "Execute body within a render pass.
   
   Usage:
   (with-pass {:clear-color [0.1 0.1 0.1 1.0]}
     (apply-pipeline my-pipeline)
     (draw 0 3))"
  [opts & body]
  `(do
     (begin-pass ~opts)
     (try
       ~@body
       (finally
         (end-pass)))))

(defmacro with-frame
  "Execute body and commit the frame.
   
   Usage:
   (with-frame
     (with-pass {}
       (draw 0 3)))"
  [& body]
  `(do
     ~@body
     (commit)))
