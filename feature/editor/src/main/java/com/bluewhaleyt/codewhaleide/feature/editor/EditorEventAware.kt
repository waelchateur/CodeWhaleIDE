package com.bluewhaleyt.codewhaleide.feature.editor

import androidx.annotation.CallSuper
import io.github.rosemoe.sora.event.ClickEvent
import io.github.rosemoe.sora.event.ColorSchemeUpdateEvent
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.DoubleClickEvent
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.event.HandleStateChangeEvent
import io.github.rosemoe.sora.event.LongPressEvent
import io.github.rosemoe.sora.event.PublishSearchResultEvent
import io.github.rosemoe.sora.event.ScrollEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.event.SnippetEvent
import io.github.rosemoe.sora.event.TextSizeChangeEvent
import io.github.rosemoe.sora.event.Unsubscribe

interface EditorEventAware {

    @CallSuper
    fun onClick(event: ClickEvent, unsubscribe: Unsubscribe)  = Unit

    @CallSuper
    fun onDoubleClick(event: DoubleClickEvent, unsubscribe: Unsubscribe)  = Unit

    @CallSuper
    fun onLongClick(event: LongPressEvent, unsubscribe: Unsubscribe)  = Unit

    @CallSuper
    fun onScrollChange(event: ScrollEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onSelectionChange(event: SelectionChangeEvent, unsubscribe: Unsubscribe)  = Unit

    @CallSuper
    fun onContentChange(event: ContentChangeEvent, unsubscribe: Unsubscribe)  = Unit

    @CallSuper
    fun onTextSizeChange(event: TextSizeChangeEvent, unsubscribe: Unsubscribe)  = Unit

    @CallSuper
    fun onSnippetStateChange(event: SnippetEvent, unsubscribe: Unsubscribe)  = Unit

    @CallSuper
    fun onSelectionHandleStateChange(event: HandleStateChangeEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onSearchResultChange(event: PublishSearchResultEvent, unsubscribe: Unsubscribe) = Unit

    @CallSuper
    fun onColorSchemeChange(event: ColorSchemeUpdateEvent, unsubscribe: Unsubscribe)  = Unit

    @CallSuper
    fun onKeyPress(event: EditorKeyEvent, unsubscribe: Unsubscribe) = Unit

}