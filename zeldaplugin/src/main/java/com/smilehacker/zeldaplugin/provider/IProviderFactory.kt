package com.smilehacker.zeldaplugin.provider

/**
 * Created by quan.zhou on 29/12/2017.
 */
interface IProviderFactory {
    fun getPluginName() : String
    fun getPluginProviderClass() : Class<*>
}