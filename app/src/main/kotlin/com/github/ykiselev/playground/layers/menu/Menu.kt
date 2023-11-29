package com.github.ykiselev.playground.layers.menu

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.common.closeables.Closeables
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.fonts.TrueTypeFont
import com.github.ykiselev.playground.ui.elements.CheckBox
import com.github.ykiselev.playground.ui.elements.Link
import com.github.ykiselev.playground.ui.elements.Slider
import com.github.ykiselev.playground.ui.menus.ListMenu
import com.github.ykiselev.playground.ui.models.checkbox.CheckBoxModel
import com.github.ykiselev.playground.ui.models.checkbox.ConfigurationBoundCheckBoxModel
import com.github.ykiselev.playground.ui.models.checkbox.SimpleCheckBoxModel
import com.github.ykiselev.playground.ui.models.slider.ConfigurationBoundSliderModel
import com.github.ykiselev.playground.ui.models.slider.SliderDefinition
import com.github.ykiselev.spi.api.Removable
import com.github.ykiselev.spi.services.commands.Commands
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import com.github.ykiselev.spi.services.layers.DrawingContext
import com.github.ykiselev.spi.services.layers.UiLayer
import com.github.ykiselev.spi.services.layers.UiLayers
import com.github.ykiselev.spi.window.DelegatingWindowEvents
import com.github.ykiselev.spi.window.WindowEvents
import org.lwjgl.glfw.GLFW

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class Menu(
    assets: Assets, configuration: PersistedConfiguration,
    commands: Commands, uiLayers: UiLayers
) : UiLayer, AutoCloseable, Removable {

    private val listMenu: ListMenu
    private var pushed = false
    private val windowEvents: WindowEvents
    private val font: TrueTypeFont
    private val closeable: AutoCloseable

    override fun canBeRemoved(): Boolean {
        return !pushed
    }

    override fun kind(): UiLayer.Kind {
        return UiLayer.Kind.POPUP
    }

    init {
        Closeables.newGuard().use { guard ->
            val atlas = assets.load("font-atlases/base.conf", OglRecipes.FONT_ATLAS)
            guard.add(atlas)
            font = atlas.value()["menu"]
            closeable = guard.detach()
        }
        val effectsSlider = Slider(
            ConfigurationBoundSliderModel(
                SliderDefinition(0, 10, 1),
                configuration.root(),
                "sound.effects.level"
            )
        )
        listMenu = ListMenu(
            ListMenu.MenuItem(
                element = Link(
                    "New"
                ) { commands.execute("new-game") }
            ),
            ListMenu.MenuItem(
                "Flag1",
                CheckBox(
                    SimpleCheckBoxModel(
                        { m: CheckBoxModel -> effectsSlider.enable(m.checked()) },
                        true
                    ) //new ConfigurationBoundCheckBoxModel(configuration, "sound.flag1"),
                )
            ),
            ListMenu.MenuItem(
                "Is Game Present?",
                CheckBox( //new SimpleCheckBoxModel(m -> effectsSlider.enable(m.checked()), true)
                    ConfigurationBoundCheckBoxModel(configuration.root(), "game.isPresent")
                )
            ),
            ListMenu.MenuItem("Effects", effectsSlider),
            ListMenu.MenuItem(
                "",
                Slider(
                    ConfigurationBoundSliderModel(
                        SliderDefinition(0, 10, 1),
                        configuration.root(),
                        "sound.music.level"
                    )
                )
            ),
            ListMenu.MenuItem(
                element = Link(
                    "Exit"
                ) { commands.execute("quit") }
            )
        )
        windowEvents = object : DelegatingWindowEvents(listMenu.events()) {
            override fun keyEvent(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
                if (super.keyEvent(key, scanCode, action, mods)) {
                    return true
                }
                if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_ESCAPE) {
                    uiLayers.pop(this@Menu)
                    return true
                }
                return true
            }
        }
    }

    override fun events(): WindowEvents =
        windowEvents

    override fun onPush() {
        pushed = true
    }

    override fun onPop() {
        pushed = false
    }

    override fun close() {
        Closeables.close(closeable)
    }

    override fun draw(width: Int, height: Int, context: DrawingContext) {
        context.textAttributes.trueTypeFont(font)
        listMenu.draw(width, height, context)
    }
}