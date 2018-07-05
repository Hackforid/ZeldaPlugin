package com.smilehacker.zeldaplugin.compiler

import com.smilehacker.zeldaplugin.annotation.ZProvider
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import javax.tools.StandardLocation

/**
 * Created by zhouquan on 17/12/29.
 */
class FileMaker(val messager: Messager, val elements: Elements, val types: Types) {

    private val PKG = "com.smilehacker.zeldaplugin.provider.plugins"
    val PluginProvider = ClassName("com.smilehacker.zeldaplugin.provider", "PluginProvider")
    val IProviderFactory = ClassName("com.smilehacker.zeldaplugin.provider", "IProviderFactory")

    fun make(filer: Filer, roundEnvironment: RoundEnvironment) {
        generateProviders(roundEnvironment, filer)
    }

    private fun generateProviders(roundEnvironment: RoundEnvironment, filer: Filer) {
        val eles = roundEnvironment.getElementsAnnotatedWith(ZProvider::class.java)
        log("generateProviders", "size = ${eles.size}")
        eles.forEach {
            ele ->
            if (ele.kind == ElementKind.CLASS) {
                makeProviderFile(filer, ele)
            }
        }
    }

    private fun makeProviderFile(filer: Filer, element: Element) {
        val annotation = element.getAnnotation(ZProvider::class.java)
        val pluginName = annotation.name
        val typeEle = element as TypeElement

        val className = "ZPlugin_$pluginName"
        val classBuilder= TypeSpec.classBuilder(className)
                            .addSuperinterface(IProviderFactory)
                            .addFunction(getPluginNameFunc(pluginName))
                            .addFunction(getPluginProviderClass(typeEle))

        val fileBuilder = FileSpec.builder(PKG, className)
                .addType(classBuilder.build())

        val fileObject = filer.createResource(StandardLocation.SOURCE_OUTPUT, PKG, "$className.kt")
        val writer = fileObject.openWriter()
        writer.use {
            writer.write(fileBuilder.build().toString())
            writer.flush()
        }
    }

    private fun getPluginNameFunc(pluginName: String) : FunSpec {
        val builder = FunSpec.builder("getPluginName")
                .addModifiers(KModifier.OVERRIDE)
                .returns(String::class)

        builder.addStatement("return \"$pluginName\"")
        return builder.build()
    }

    private fun getPluginProviderClass(typeElement: TypeElement) : FunSpec {
        // <T: PluginProvider>
//        val typeVariable = TypeVariableName.invoke("T",  PluginProvider)
        // Class<T>
        val classTName = ParameterizedTypeName.get(ClassName("", "Class"), TypeVariableName.invoke("*"))


        val builder = FunSpec.builder("getPluginProviderClass")
                .addModifiers(KModifier.OVERRIDE)
//                .addTypeVariable(typeVariable)
                .returns(classTName)


        builder.addStatement("""return %T::class.java""", typeElement)
        return builder.build()

    }

    private fun error(e: Element, msg: String, vararg args: Any) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, *args),
                e)
    }

    private fun log(e: Element, msg: String, vararg args: Any) {
        messager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, *args),
                e)
    }

    private fun log(msg: String, vararg args: Any) {
        messager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, *args)
        )
    }
}