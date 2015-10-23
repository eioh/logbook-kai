package logbook.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * プラグインを管理します
 *
 */
public class PluginContainer {

    private static final PluginContainer container = new PluginContainer();

    private List<JarBasedPlugin> plugins;

    private URLClassLoader classLoader;

    private boolean initialized;

    private PluginContainer() {
    }

    void init(List<JarBasedPlugin> plugins) {
        URL[] urls = plugins.stream()
                .map(JarBasedPlugin::getURL)
                .toArray(URL[]::new);
        this.plugins = new ArrayList<>(plugins);
        this.classLoader = new URLClassLoader(urls);
        this.initialized = true;
    }

    void close() throws IOException {
        this.classLoader.close();
    }

    /**
     * このプラグインコンテナーが読み込んでいるプラグインのリストを返します
     * @return
     */
    public List<JarBasedPlugin> getPlugins() {
        if (!this.initialized) {
            throw new IllegalStateException("PluginContainer not initialized");
        }
        return this.plugins;
    }

    /**
     * このプラグインコンテナーのクラスローダーを返します
     * @return
     */
    public ClassLoader getClassLoader() {
        if (!this.initialized) {
            throw new IllegalStateException("PluginContainer not initialized");
        }
        return this.classLoader;
    }

    /**
     * プラグインコンテナーのインスタンスを返します
     * @return プラグインコンテナーのインスタンス
     */
    public static PluginContainer getInstance() {
        return container;
    }
}
