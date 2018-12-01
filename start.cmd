"%JAVA_HOME%\bin\java" --module-path app/target/playground/bin;app/target/playground/base ^
 -Dorg.lwjgl.util.Debug=true ^
 -Dorg.lwjgl.util.DebugLoader=true ^
 -Dorg.lwjgl.librarypath=app/target/playground/bin ^
 -m ykiselev.playground.app/com.github.ykiselev.playground.Main ^
 assets=app/target/playground/base/data ^
 home=app/target/playground/base ^
