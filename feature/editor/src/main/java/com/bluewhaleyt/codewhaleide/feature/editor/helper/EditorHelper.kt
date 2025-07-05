package com.bluewhaleyt.codewhaleide.feature.editor.helper

import android.graphics.Typeface
import com.bluewhaleyt.codewhaleide.common.extension.runSafe
import com.bluewhaleyt.codewhaleide.feature.editor.BaseEditor
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class EditorHelper internal constructor(
    private val editor: BaseEditor
) {

    fun setTypeface(fileName: String) {
        val typeface = Typeface.createFromAsset(editor.context.assets, fileName)
        editor.typefaceText = typeface
        editor.typefaceLineNumber = typeface
    }

    suspend fun setTextMateTheme(fileName: String) {
        ThemeRegistry.getInstance().setTheme(fileName)
        // The theme requires the editor to update manually
        delay(1.milliseconds)
        editor.setText(editor.text)
    }

    suspend fun setTextMateLanguage(fileExtension: String) {
        runSafe {
            val scopeName = EditorGrammarHelper.findScopeByFileExtension(fileExtension)
            scopeName?.let {
                editor.setEditorLanguage(TextMateLanguage.create(it, true))
            }
        } ?: editor.setEditorLanguage(TextMateLanguage.create("text.plain", true))
    }

}