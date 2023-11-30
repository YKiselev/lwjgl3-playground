package com.github.ykiselev.playground

import com.github.ykiselev.common.closeables.Closeables
import com.github.ykiselev.common.fps.FrameInfo
import com.github.ykiselev.playground.app.window.AppWindow
import com.github.ykiselev.playground.app.window.WindowBuilder
import com.github.ykiselev.playground.init.*
import com.github.ykiselev.playground.layers.AppUiLayers
import com.github.ykiselev.playground.services.AppContext
import com.github.ykiselev.playground.services.AppMenu
import com.github.ykiselev.playground.services.AppSprites
import com.github.ykiselev.playground.services.GameBootstrap
import com.github.ykiselev.playground.services.assets.GameAssets
import com.github.ykiselev.playground.services.config.AppConfig
import com.github.ykiselev.playground.services.console.AppCommands
import com.github.ykiselev.playground.services.console.ConsoleFactory
import com.github.ykiselev.playground.services.console.DefaultTokenizer
import com.github.ykiselev.playground.services.fs.AppFileSystem
import com.github.ykiselev.playground.services.fs.ClassPathResources
import com.github.ykiselev.playground.services.fs.DiskResources
import com.github.ykiselev.playground.services.schedule.AppSchedule
import com.github.ykiselev.playground.services.sound.AppSoundEffects
import com.github.ykiselev.spi.MonitorInfo
import com.github.ykiselev.spi.ProgramArguments
import com.github.ykiselev.spi.services.layers.UiLayers
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class Main(private val arguments: ProgramArguments) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val exitFlag = AtomicBoolean()

    private fun run() {
        try {
            Closeables.newGuard().use { guard ->
                guard.add(GlfwBootstrap())
                initStdOut()
                guard.add(ErrorCallbackBootstrap())
                val fileSystem = guard.add(
                    AppFileSystem(
                        DiskResources(arguments.assetPaths()),
                        ClassPathResources(Main::class.java.getClassLoader())
                    )
                )
                val monitorInfo = MonitorInfoFactory.fromIndex(arguments.monitor)
                val assets = guard.add(GameAssets.create(fileSystem, monitorInfo))
                val commands = guard.add(AppCommands(DefaultTokenizer()))
                val config = guard.add(AppConfig(fileSystem))
                val schedule = guard.add(AppSchedule())
                val uiLayers = guard.add(AppUiLayers())
                val soundEffects = guard.add(AppSoundEffects(config))
                val window = guard.add(createWindow(monitorInfo, uiLayers))
                window.show()
                window.makeCurrent()
                GLFW.glfwSwapInterval(arguments.swapInterval)
                val spriteBatch = guard.add(AppSprites.createBatch(assets))
                val context = AppContext(
                    arguments,
                    fileSystem,
                    commands,
                    config,
                    schedule,
                    uiLayers,
                    assets,
                    spriteBatch,
                    soundEffects,
                    window,
                    FrameInfo(60)
                )
                guard.add(
                    ConsoleFactory.create(
                        config, commands, uiLayers, assets
                    )
                )
                guard.add(
                    AppMenu(
                        assets, config, commands, uiLayers, schedule
                    )
                )
                val game = guard.add(GameBootstrap(context))
                val task = FrameInfoTracker(
                    ScheduleTask(
                        WindowTask(
                            GameTask(
                                UiLayersTask(
                                    null, uiLayers, spriteBatch
                                ),
                                game
                            ),
                            window
                        ),
                        schedule
                    ),
                    context.frameInfo
                )
                guard.add(context.commands.add("quit", ::onQuit))
                logger.info("Entering main loop...")
                // todo - remove that (why?)
                context.commands.execute("new-game")
                //
                while (!window.shouldClose() && !exitFlag.get()) {
                    task.run()
                }
                context.configuration.persist()
            }
        } catch (e: Exception) {
            logger.error("Unhandled exception!", e)
            exitProcess(-1)
        }
    }

    private fun createWindow(monitorInfo: MonitorInfo, uiLayers: UiLayers): AppWindow {
        return WindowBuilder()
            .fullScreen(arguments.fullScreen)
            .version(3, 3)
            .coreProfile()
            .debug(arguments.debug)
            .monitor(monitorInfo.monitor)
            .dimensions(800, 600)
            .events(uiLayers.events())
            .build("LWJGL Playground")
    }

    private fun onQuit() {
        logger.info("Exiting app...")
        exitFlag.set(true)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main(ProgramArguments(args)).run()
        }
    }
}