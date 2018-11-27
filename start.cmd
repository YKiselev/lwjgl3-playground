"%JAVA_HOME%\bin\java" --module-path app/target/playground/bin;app/target/playground/base ^
 -XX:MaxGCPauseMillis=3 ^
 -Dorg.lwjgl.util.Debug=true ^
 -Dorg.lwjgl.util.DebugLoader=true ^
 -Dorg.lwjgl.librarypath=app/target/playground/bin ^
 -m ykiselev.playground.app/com.github.ykiselev.playground.Main ^
 asset.paths="D:\Downloads\lwjgl3-app-assets,d:\tmp\x y z" ^
 mod.home=D:\Downloads\lwjgl-home\base ^
