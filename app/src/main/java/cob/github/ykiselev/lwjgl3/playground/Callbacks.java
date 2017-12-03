package cob.github.ykiselev.lwjgl3.playground;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Callbacks implements WindowCallbacks {

    private Game game;

    public void target(Game game) {
        this.game = game;
    }

    @Override
    public void keyEvent(int key, int scanCode, int action, int mods) {
        if (game != null) {
            game.keyEvent(key, scanCode, action, mods);
        }
    }

    @Override
    public void cursorEvent(double x, double y) {
        if (game != null) {
            game.cursorEvent(x, y);
        }
    }

    @Override
    public void mouseButtonEvent(int button, int action, int mods) {
        if (game != null) {
            game.mouseButtonEvent(button, action, mods);
        }
    }

    @Override
    public void frameBufferEvent(int width, int height) {
        if (game != null) {
            game.frameBufferEvent(width, height);
        }
    }
}
