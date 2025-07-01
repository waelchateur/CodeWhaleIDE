package com.bluewhaleyt.codewhaleide.feature.editor

import io.github.rosemoe.sora.text.Content

class EditorController internal constructor(
    private val editor: BaseEditor
) {

    fun getSelectedText(): Content = with(editor.cursor) {
        editor.text.subContent(leftLine, leftColumn, rightLine, rightColumn)
    }

}