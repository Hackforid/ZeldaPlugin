package com.smilehacker.zeldaplugin.compiler


import com.smilehacker.zeldaplugin.annotation.ZProvider
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * Created by zhouquan on 17/12/28.
 */
//@AutoService(Processor::class)
class ZeldaProcessor : AbstractProcessor() {

    private lateinit var mTypeUtils: Types
    private lateinit var mElementUtils: Elements
    private lateinit var mFiler: Filer
    private lateinit var mMessager: Messager

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        mTypeUtils = processingEnvironment.typeUtils
        mElementUtils = processingEnvironment.elementUtils
        mFiler = processingEnvironment.filer
        mMessager = processingEnvironment.messager
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        FileMaker(mMessager, mElementUtils, mTypeUtils).make(mFiler,roundEnvironment)
        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ZProvider::class.java.name)
    }

    private fun error(e: Element?, msg: String, vararg args: Any) {
        mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, *args),
                e)
    }

    private fun log(e: Element?, msg: String, vararg args: Any) {
        mMessager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, *args),
                e)
    }

}