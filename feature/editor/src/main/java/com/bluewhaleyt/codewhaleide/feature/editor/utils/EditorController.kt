package com.bluewhaleyt.codewhaleide.feature.editor.utils

import com.bluewhaleyt.codewhaleide.common.extension.runSafe
import com.bluewhaleyt.codewhaleide.feature.editor.BaseEditor
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.text.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class EditorController internal constructor(
    private val editor: BaseEditor
) {

    private val themeRegistry = ThemeRegistry.getInstance()

    fun getSelectedText(): Content = with(editor.cursor) {
        editor.text.subContent(leftLine, leftColumn, rightLine, rightColumn)
    }

    suspend fun setTextMateTheme(fileName: String) {
        themeRegistry.setTheme(fileName)
        delay(1.milliseconds)
        editor.setText(editor.text)
    }

    suspend fun setTextMateLanguage(fileExtension: String) {
        coroutineScope {
            launch(Dispatchers.IO) {
                runSafe {
                    val scopeName = EditorGrammarHelper.findScopeByFileExtension(fileExtension)
                    scopeName?.let {
                        editor.setEditorLanguage(TextMateLanguage.create(it, true))
                    }
                } ?: editor.setEditorLanguage(TextMateLanguage.create("text.plain", true))
            }
        }
    }

}