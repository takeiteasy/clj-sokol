(ns clj-sokol.audio
  "Audio playback wrapper for sokol_audio.
   
   Provides functions for audio initialization and sample playback."
  (:import [org.sokol.wrapper Audio Audio$Config$Builder]))

;; =============================================================================
;; Setup
;; =============================================================================

(defn setup
  "Initialize the audio system.
   
   Options:
   - :sample-rate - sample rate in Hz (default: 44100)
   - :channels - number of channels (default: 1)
   - :buffer-frames - buffer size in frames (default: 2048)
   
   Returns true if setup was successful.
   
   Example:
   (setup {:sample-rate 44100 :channels 2})"
  ([]
   (Audio/setup))
  ([{:keys [sample-rate channels buffer-frames]
     :or {sample-rate 44100 channels 1 buffer-frames 2048}}]
   (let [config (-> (Audio$Config$Builder.)
                    (.sampleRate (int sample-rate))
                    (.channels (int channels))
                    (.bufferFrames (int buffer-frames))
                    (.build))]
     (Audio/setup config))))

(defn shutdown
  "Shut down the audio system."
  []
  (Audio/shutdown))

(defn valid?
  "Check if the audio system is valid."
  []
  (Audio/isValid))

;; =============================================================================
;; Audio Properties
;; =============================================================================

(defn sample-rate
  "Get the audio sample rate."
  []
  (Audio/sampleRate))

(defn channels
  "Get the number of audio channels."
  []
  (Audio/channels))

(defn buffer-frames
  "Get the buffer size in frames."
  []
  (Audio/bufferFrames))

(defn suspended?
  "Check if audio is suspended."
  []
  (Audio/isSuspended))

;; =============================================================================
;; Push Mode
;; =============================================================================

(defn expect
  "Get the number of frames that can be pushed.
   Returns 0 if no frames are needed."
  []
  (Audio/expect))

(defn push
  "Push audio samples in push mode.
   - samples: float array of interleaved samples
   Returns the number of frames actually pushed."
  [^floats samples]
  (Audio/push samples))

;; =============================================================================
;; Convenience Functions
;; =============================================================================

(defn make-sine-wave
  "Generate a sine wave buffer.
   Returns a float array suitable for pushing.
   
   Options:
   - :frequency - frequency in Hz
   - :amplitude - amplitude (0.0 to 1.0, default: 0.5)
   - :num-frames - number of frames to generate
   - :phase - starting phase (default: 0)"
  [{:keys [frequency amplitude num-frames phase]
    :or {amplitude 0.5 phase 0}}]
  (let [sr (sample-rate)
        ch (channels)
        buffer (float-array (* num-frames ch))
        phase-inc (/ (* 2.0 Math/PI frequency) sr)]
    (loop [i 0
           p phase]
      (when (< i num-frames)
        (let [sample (float (* amplitude (Math/sin p)))]
          (dotimes [c ch]
            (aset buffer (+ (* i ch) c) sample))
          (recur (inc i) (+ p phase-inc)))))
    buffer))

(defmacro with-audio
  "Setup audio, execute body, then shutdown.
   
   Usage:
   (with-audio {:sample-rate 44100}
     (push-samples ...))"
  [opts & body]
  `(try
     (setup ~opts)
     ~@body
     (finally
       (shutdown))))
