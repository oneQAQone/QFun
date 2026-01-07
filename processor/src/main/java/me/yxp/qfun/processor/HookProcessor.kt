package me.yxp.qfun.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

class HookProcessor(
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    private val annotationName = "me.yxp.qfun.annotation.HookItemAnnotation"

    private val baseHookItemPackage = "me.yxp.qfun.hook.base"
    private val baseHookItemName = "BaseHookItem"

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        val validSymbols = ArrayList<KSClassDeclaration>()
        val deferredSymbols = ArrayList<KSAnnotated>()

        for (symbol in symbols) {
            if (symbol.validate()) {
                if (!symbol.modifiers.contains(Modifier.ABSTRACT)) {
                    validSymbols.add(symbol)
                }
            } else {
                deferredSymbols.add(symbol)
            }
        }

        if (validSymbols.isNotEmpty()) {
            generateHookRegistry(validSymbols)
        }

        return deferredSymbols
    }

    private fun generateHookRegistry(classes: List<KSClassDeclaration>) {
        val packageName = "me.yxp.qfun.generated"
        val className = "HookRegistry"

        val baseHookItemType = ClassName(baseHookItemPackage, baseHookItemName)
        val listType = ClassName("kotlin.collections", "List")
            .parameterizedBy(baseHookItemType)


        val initializerBlock = CodeBlock.builder()
            .add("listOf(\n")
            .indent()
            .apply {
                classes.forEach { ksClass ->
                    add("%T,\n", ksClass.toClassName())
                }
            }
            .unindent()
            .add(")")
            .build()

        val hookItemsProp = PropertySpec.builder("hookItems", listType)
            .initializer(initializerBlock)
            .build()

        val fileSpec = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.objectBuilder(className)
                    .addProperty(hookItemsProp)
                    .addKdoc("此文件由 KSP 自动生成，请勿修改。包含所有检测到的 HookItem。")
                    .build()
            )
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(true, *classes.mapNotNull { it.containingFile }.toTypedArray()))
    }
}

class HookProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return HookProcessor(environment.codeGenerator)
    }
}