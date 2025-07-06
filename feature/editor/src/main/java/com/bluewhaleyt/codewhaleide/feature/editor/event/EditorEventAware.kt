package com.bluewhaleyt.codewhaleide.feature.editor.event

import androidx.annotation.CallSuper
import io.github.rosemoe.sora.event.ClickEvent
import io.github.rosemoe.sora.event.ColorSchemeUpdateEvent
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.ContextClickEvent
import io.github.rosemoe.sora.event.DoubleClickEvent
import io.github.rosemoe.sora.event.EventManager
import io.github.rosemoe.sora.event.HoverEvent
import io.github.rosemoe.sora.event.LongPressEvent
import io.github.rosemoe.sora.event.ScrollEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.event.Unsubscribe
import io.github.rosemoe.sora.event.subscribeEvent
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.subscribeEvent

interface EditorEventAware {

    // Built-in

    @CallSuper
    fun onEditorClick(event: ClickEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorDoubleClick(event: DoubleClickEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorLongClick(event: LongPressEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorMouseRightClick(event: ContextClickEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorMouseHover(event: HoverEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorColorSchemeChange(event: ColorSchemeUpdateEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorScrollChange(event: ScrollEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorSelectionChange(event: SelectionChangeEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorContentChange(event: ContentChangeEvent, unsubscribe: Unsubscribe) = Unit

    // Custom

    @CallSuper
    fun onEditorTouch(event: EditorTouchEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onEditorSearchChange(event: EditorSearchEvent, unsubscribe: Unsubscribe) = Unit

}

internal fun EditorEventAware.subscribeAllEvents(target: Any) {
    require(target is CodeEditor || target is EventManager)

    when (target) {
        is CodeEditor -> {
            target.apply {
                subscribeEvent(::onEditorClick)
                subscribeEvent(::onEditorDoubleClick)
                subscribeEvent(::onEditorLongClick)
                subscribeEvent(::onEditorMouseRightClick)
                subscribeEvent(::onEditorMouseHover)
                subscribeEvent(::onEditorColorSchemeChange)
                subscribeEvent(::onEditorScrollChange)
                subscribeEvent(::onEditorSelectionChange)
                subscribeEvent(::onEditorContentChange)

                subscribeEvent(::onEditorTouch)
                subscribeEvent(::onEditorSearchChange)
            }
        }
        is EventManager -> {
            target.apply {
                subscribeEvent(::onEditorClick)
                subscribeEvent(::onEditorDoubleClick)
                subscribeEvent(::onEditorLongClick)
                subscribeEvent(::onEditorMouseRightClick)
                subscribeEvent(::onEditorMouseHover)
                subscribeEvent(::onEditorColorSchemeChange)
                subscribeEvent(::onEditorScrollChange)
                subscribeEvent(::onEditorSelectionChange)
                subscribeEvent(::onEditorContentChange)

                subscribeEvent(::onEditorTouch)
                subscribeEvent(::onEditorSearchChange)
            }
        }
    }
}