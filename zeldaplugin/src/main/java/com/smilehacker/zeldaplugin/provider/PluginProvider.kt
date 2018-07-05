package com.smilehacker.zeldaplugin.provider

import android.net.Uri

/**
 * Created by quan.zhou on 2017/8/7.
 */
abstract class PluginProvider : IPluginAction {

    /**
     * first init when load
     */
    abstract fun initialize()

    /**
     * lazy init when first call
     */
    abstract fun lazyInitialize()

    private var mIsLazyInitialized = false

    internal fun doQuery(uri : Uri, vararg params: Any?) : Any? {
        if (!mIsLazyInitialized) {
            synchronized(this) {
                if (!mIsLazyInitialized) {
                    lazyInitialize()
                    mIsLazyInitialized = true
                }
            }
        }
        return query(uri, *params)
    }

    protected val context = PluginApp.INSTANCE
}