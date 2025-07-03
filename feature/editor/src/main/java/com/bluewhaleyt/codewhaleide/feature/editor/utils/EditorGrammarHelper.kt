package com.bluewhaleyt.codewhaleide.feature.editor.utils

import android.content.Context
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.eclipse.tm4e.core.registry.IGrammarSource
import java.nio.charset.Charset

internal object EditorGrammarHelper {

    private val grammarRegistry = GrammarRegistry.getInstance()
    private var _grammars: List<GrammarModel> = mutableListOf()

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        allowTrailingComma = true
    }

    val grammars: List<GrammarModel>
        get() = _grammars

    fun init(context: Context, assetPath: String) {
        if (_grammars.isNotEmpty()) {
            return
        }

        val grammarsJson = context.assets.open(assetPath).bufferedReader()
            .use { it.readText() }
        _grammars = json.decodeFromString<List<GrammarModel>>(grammarsJson)
    }

    suspend fun findScopeByFileExtension(extension: String?): String? {
        val grammar = findGrammarByFileExtension(extension) ?: return null
        if (grammarRegistry.findGrammar(grammar.scopeName) == null) {
            registerGrammar(grammar)
        }
        return grammar.scopeName
    }

    private suspend fun registerGrammarByFileExtension(extension: String?) {
        val grammar = findGrammarByFileExtension(extension) ?: return
        registerGrammar(grammar)
    }

    private suspend fun registerGrammar(grammar: GrammarModel) {
        if (grammarRegistry.findGrammar(grammar.scopeName) == null) {
            registerEmbeddedLanguagesGrammar(grammar)

            val grammarPath = "editor/textmate/language/${grammar.name}/syntaxes/${grammar.grammar}"
            val grammarSource = IGrammarSource.fromInputStream(
                FileProviderRegistry.getInstance().tryGetInputStream(grammarPath),
                grammarPath,
                Charset.defaultCharset(),
            )

            val languageConfigPath = grammar.languageConfiguration
                ?: "editor/textmate/language/${grammar.name}/language-configuration.json"

            grammarRegistry.loadGrammar(
                DefaultGrammarDefinition.withLanguageConfiguration(
                    grammarSource,
                    languageConfigPath,
                    grammar.name,
                    grammar.scopeName,
                ).withEmbeddedLanguages(grammar.embeddedLanguages)
            )
        }
    }

    private suspend fun registerEmbeddedLanguagesGrammar(grammar: GrammarModel) {
        val embeddedLanguages = grammar.embeddedLanguages ?: return

        for ((_, name) in embeddedLanguages) {
            val embeddedGrammar = findGrammarByName(name)
            if (embeddedGrammar != null) {
                registerGrammar(embeddedGrammar)
            }
        }
    }

    private fun findGrammarByFileExtension(extension: String?): GrammarModel? =
        _grammars.find { it.alias?.contains(extension) ?: false }

    private fun findGrammarByName(name: String): GrammarModel? = _grammars.find { it.name == name }

    private fun findGrammarByScope(scopeName: String): GrammarModel? =
        _grammars.find { it.scopeName == scopeName }

    @Serializable
    class GrammarModel(
        val name: String,
        val scopeName: String,
        val grammar: String,
        val languageConfiguration: String? = null,
        val embeddedLanguages: Map<String, String>? = null,
        val alias: Array<String>? = null,
    )
}