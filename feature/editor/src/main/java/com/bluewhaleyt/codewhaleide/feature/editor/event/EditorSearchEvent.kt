package com.bluewhaleyt.codewhaleide.feature.editor.event

import com.bluewhaleyt.codewhaleide.feature.editor.BaseEditor
import io.github.rosemoe.sora.event.Event
import io.github.rosemoe.sora.event.PublishSearchResultEvent
import io.github.rosemoe.sora.widget.subscribeAlways

class EditorSearchEvent(
    editor: BaseEditor,
    val query: String
) : Event(editor) {

    companion object {
        fun setup(editor: BaseEditor) {
            editor.subscribeAlways<PublishSearchResultEvent> {
                editor.searcher.query?.let {
                    dispatch(editor, it)
                }
            }
        }

        internal fun dispatch(editor: BaseEditor, query: String) {
            val thisEvent = EditorSearchEvent(editor, query)
            editor.dispatchEvent(thisEvent)
        }
    }

}