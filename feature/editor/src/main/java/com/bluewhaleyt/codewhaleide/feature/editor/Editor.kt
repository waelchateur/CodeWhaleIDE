package com.bluewhaleyt.codewhaleide.feature.editor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import kotlinx.coroutines.launch

@SuppressLint("ViewConstructor")
class Editor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : BaseEditor(context, attrs, defStyleAttr, defStyleRes) {

    init {
        scope.launch {
            helper.setTextMateTheme("darcula")
            helper.setTextMateLanguage("java")
        }
    }

}