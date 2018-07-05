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

    private val mPlugins = HashMap<String, PluginProvider>() // not use ArrayMap because of it need support lib

    fun load(pluginName: String) {
        try {
            val clazz = Class.forName("com.smilehacker.zeldaplugin.provider.plugins.ZPlugin_$pluginName")
            val factory = clazz.newInstance() as IProviderFactory
            val plugin = factory.getPluginProviderClass().newInstance() as PluginProvider
            if (mPlugins[factory.getPluginName()] == null) {
                plugin.initialize()
            }
            mPlugins[factory.getPluginName()] = plugin
        } catch (e: Throwable) {
            Log.e("Zelda", "load error", e)
        }
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
}