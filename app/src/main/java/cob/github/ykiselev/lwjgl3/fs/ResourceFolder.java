package cob.github.ykiselev.lwjgl3.fs;

import java.net.URL;
import java.util.Optional;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ResourceFolder {

    Optional<URL> resolve(String resource);
}
