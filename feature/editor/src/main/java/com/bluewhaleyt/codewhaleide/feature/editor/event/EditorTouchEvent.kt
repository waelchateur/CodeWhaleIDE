package com.bluewhaleyt.codewhaleide.feature.editor.event

import com.bluewhaleyt.codewhaleide.feature.editor.BaseEditor
import io.github.rosemoe.sora.event.ClickEvent
import io.github.rosemoe.sora.event.DoubleClickEvent
import io.github.rosemoe.sora.event.EditorMotionEvent
import io.github.rosemoe.sora.event.LongPressEvent
import io.github.rosemoe.sora.widget.subscribeAlways

class EditorTouchEvent internal constructor(
    event: EditorMotionEvent
) : EditorMotionEvent(event.editor, event.charPosition, event.causingEvent, event.span, event.spanRange, event.motionRegion, event.motionBound) {

    companion object {
        internal fun setup(editor: BaseEditor) {
            editor.apply {
                subscribeAlways<ClickEvent> { event ->
                    dispatch(editor, event)
                }
                subscribeAlways<DoubleClickEvent> { event ->
                    dispatch(editor, event)
                }
                subscribeAlways<LongPressEvent> { event ->
                    dispatch(editor, event)
                }
            }
        }

        private fun dispatch(editor: BaseEditor, event: EditorMotionEvent) {
            val thisEvent = EditorTouchEvent(event)
            editor.dispatchEvent(thisEvent)
        }
    }

}