package com.bluewhaleyt.codewhaleide.feature.editor

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
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
            val isDark = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
            val theme = if (isDark) "darcula" else "quietlight"

            helper.setTextMateTheme(theme)
            helper.setTextMateLanguage("java")
        }
    }

}