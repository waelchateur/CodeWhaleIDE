package com.bluewhaleyt.codewhaleide.feature.editor

import android.app.Application
import com.bluewhaleyt.codewhaleide.feature.editor.utils.EditorGrammarHelper
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.langs.textmate.registry.provider.FileResolver
import org.eclipse.tm4e.core.registry.IThemeSource

open class EditorApplication : Application() {
    private val fileProviderRegistry = FileProviderRegistry.getInstance()
    private val themeRegistry = ThemeRegistry.getInstance()

    override fun onCreate() {
        super.onCreate()
        EditorGrammarHelper.init(this, "$TEXTMATE_PARENT_DIR/language/languages.json")
        loadThemes()
    }

    private fun loadThemes() {
        fileProviderRegistry.apply {
            dispose()
            addFileProvider(AssetsFileResolver(assets))
            addFileProvider(FileResolver.DEFAULT)
        }

        val themes = assets.list("$TEXTMATE_PARENT_DIR/theme")?.asList()
            ?.map { it.substringBeforeLast(".") }

        themes?.forEach { name ->
            val path = "$TEXTMATE_PARENT_DIR/theme/$name.json"
            themeRegistry.loadTheme(path)
        }
    }

    companion object {
        private const val TEXTMATE_PARENT_DIR = "editor/textmate"
    }
}

private fun ThemeRegistry.loadTheme(path: String) =
    FileProviderRegistry.getInstance().tryGetInputStream(path)?.let { inputStream ->
        loadTheme(ThemeModel(IThemeSource.fromInputStream(inputStream, path, null)))
    }