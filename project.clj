(defproject clj-sokol "0.1.0-SNAPSHOT"
  :description "Clojure wrapper for Sokol graphics libraries"
  :url "https://github.com/takeiteasy/clj-sokol"
  :license {:name "GPL-3.0"
            :url "https://opensource.org/licenses/GPL-3.0"}
  
  :dependencies [[org.clojure/clojure "1.11.1"]]
  
  ;; Java source paths - include the jsokol bindings
  :java-source-paths ["./jsokol/src/main/java"]
  
  ;; Compile Java sources before Clojure
  :prep-tasks ["javac" "compile"]
  
  ;; JVM options for Panama FFM
  ;; -XstartOnFirstThread is required for macOS window creation
  :jvm-opts ["-XstartOnFirstThread"
             "--enable-native-access=ALL-UNNAMED"
             "-Djava.library.path=."]
  
  ;; Source paths
  :source-paths ["src"]
  :test-paths ["test"]
  
  ;; Require Java 22+
  :javac-options ["-source" "22" "-target" "22"]
  
  ;; REPL configuration
  :repl-options {:init-ns clj-sokol.core}
  
  ;; Profiles
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "1.4.4"]]}
             :uberjar {:aot :all}})
