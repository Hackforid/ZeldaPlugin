package com.smilehacker.zeldaplugin.provider

import android.net.Uri
import android.util.Log


/**
 * Created by quan.zhou on 2017/8/7.
 */
object Zelda : IPluginAction {

    /**
     * TODO
     * 1. 通过字节码减少反射
     * 3. 后期通过dexload动态加载
     */
    private const val DEFAULT_SCHEME = "app"

    private var mPlugins : MutableMap<String, Plugin> = HashMap() // not use ArrayMap because of it need support lib

    @Synchronized
    fun load(pluginName: String) : Plugin? {
        var plugin = mPlugins[pluginName]
        if (plugin != null) {
            return plugin
        }
        try {
            val clazz = Class.forName("com.smilehacker.zeldaplugin.provider.plugins.ZPlugin_$pluginName")
            val factory = clazz.newInstance() as IProviderFactory
            plugin = factory.getPluginProviderClass().newInstance() as Plugin
            plugin.initialize()
            saveLoadedPlugin(pluginName, plugin)
        } catch (e: Throwable) {
            Log.e("Zelda", "load error", e)
        }

        return plugin
    }

    override fun query(uri: Uri, vararg params: Any?): Any? {
        val moduleName = uri.host
        val provider = mPlugins[moduleName] ?: return null
        return provider.doQuery(uri, *params)
    }

    fun query(moduleName : String, path : String, vararg params: Any?): Any? {
        var fixedPath = "$moduleName/$path".replace("//", "/")
        val uri = Uri.parse("$DEFAULT_SCHEME://$fixedPath")
        return query(uri, *params)
    }

    private fun saveLoadedPlugin(name: String, plugin: Plugin) {
        // plugin不会的太多 所以用copy的方式可以减少多线程安全的消耗
        val map = HashMap<String, Plugin>(mPlugins.size + 1)
        map.putAll(mPlugins)
        map.put(name, plugin)
        mPlugins = map
    }
}