(ns clj-sokol.app
  "Application framework wrapper for sokol_app.
   
   Provides functions for window management, input handling, and
   application lifecycle."
  (:import [org.sokol.wrapper App App$Config$Builder]
           [org.sokol.bindings SokolApp]))

;; =============================================================================
;; Application Lifecycle
;; =============================================================================

(defn run
  "Run the application with the given callbacks.
   
   Options:
   - :width - window width (default: 640)
   - :height - window height (default: 480)
   - :title - window title (default: \"clj-sokol\")
   
   Callbacks (as functions):
   - :init - called once at startup
   - :frame - called every frame
   - :cleanup - called at shutdown
   
   Example:
   (run {:width 800
         :height 600
         :title \"My App\"
         :init (fn [] (gfx/setup))
         :frame (fn [] 
                  (gfx/begin-pass {})
                  (gfx/end-pass)
                  (gfx/commit))
         :cleanup (fn [] (gfx/shutdown))})"
  [{:keys [width height title init frame cleanup]
    :or {width 640
         height 480
         title "clj-sokol"}}]
  (let [config (-> (App/config)
                   (.width (int width))
                   (.height (int height))
                   (.title title))]
    ;; Set callbacks
    (when init
      (.onInit config (reify Runnable (run [_] (init)))))
    (when frame
      (.onFrame config (reify Runnable (run [_] (frame)))))
    (when cleanup
      (.onCleanup config (reify Runnable (run [_] (cleanup)))))
    
    ;; Run the app
    (App/run (.build config))))

;; =============================================================================
;; Window Properties
;; =============================================================================

(defn width
  "Get the current framebuffer width in pixels."
  []
  (SokolApp/width))

(defn height
  "Get the current framebuffer height in pixels."
  []
  (SokolApp/height))

(defn dpi-scale
  "Get the DPI scale factor."
  []
  (SokolApp/dpiScale))

(defn high-dpi?
  "Check if running in high-DPI mode."
  []
  (SokolApp/highDpi))

(defn fullscreen?
  "Check if running in fullscreen mode."
  []
  (SokolApp/isFullscreen))

(defn toggle-fullscreen
  "Toggle fullscreen mode."
  []
  (SokolApp/toggleFullscreen))

;; =============================================================================
;; Input
;; =============================================================================

(defn mouse-shown?
  "Check if the mouse cursor is visible."
  []
  (SokolApp/mouseShown))

(defn show-mouse
  "Show or hide the mouse cursor."
  [show?]
  (SokolApp/showMouse (boolean show?)))

(defn mouse-locked?
  "Check if the mouse is locked."
  []
  (SokolApp/mouseLocked))

(defn lock-mouse
  "Lock or unlock the mouse cursor."
  [lock?]
  (SokolApp/lockMouse (boolean lock?)))

;; =============================================================================
;; Application Control
;; =============================================================================

(defn request-quit
  "Request the application to quit."
  []
  (SokolApp/requestQuit))

(defn quit
  "Immediately quit the application."
  []
  (SokolApp/quit))

(defn frame-count
  "Get the current frame count."
  []
  (SokolApp/frameCount))

(defn frame-duration
  "Get the duration of the last frame in seconds."
  []
  (SokolApp/frameDuration))
