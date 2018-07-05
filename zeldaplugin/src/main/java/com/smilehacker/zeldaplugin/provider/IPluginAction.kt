package com.smilehacker.zeldaplugin.provider

import android.net.Uri

/**
 * Created by quan.zhou on 2017/8/7.
 */
interface IPluginAction {
    fun query(uri : Uri, vararg params: Any?) : Any?
}