package com.bluewhaleyt.codewhaleide.feature.editor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.SoundEffectConstants
import com.bluewhaleyt.codewhaleide.common.extension.runSafe
import io.github.rosemoe.sora.event.ClickEvent
import io.github.rosemoe.sora.event.EditorMotionEvent
import io.github.rosemoe.sora.event.InterceptTarget
import io.github.rosemoe.sora.event.LongPressEvent
import io.github.rosemoe.sora.widget.subscribeAlways
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("ViewConstructor")
class Editor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : BaseEditor(context, attrs, defStyleAttr, defStyleRes) {

    init {
        scope.launch {
            controller.setTextMateTheme("darcula")
            controller.setTextMateLanguage("java")
        }
    }

}