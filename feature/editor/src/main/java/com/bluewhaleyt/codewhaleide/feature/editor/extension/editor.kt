@file:JvmMultifileClass
@file:JvmName("EditorUtilsKt")

package com.bluewhaleyt.codewhaleide.feature.editor.extension

import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor

val CodeEditor.selectedText: Content
    get() = text.subContent(cursor.leftLine, cursor.leftColumn, cursor.rightLine, cursor.rightColumn)