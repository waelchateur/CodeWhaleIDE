package com.bluewhaleyt.codewhaleide.feature.editor.popup

import android.util.Size
import android.view.View
import androidx.compose.ui.geometry.Offset
import com.bluewhaleyt.codewhaleide.common.extension.runSafe
import com.bluewhaleyt.codewhaleide.feature.editor.BaseEditor
import com.bluewhaleyt.codewhaleide.feature.editor.event.EditorEventAware
import com.bluewhaleyt.codewhaleide.feature.editor.event.EditorTouchEvent
import com.bluewhaleyt.codewhaleide.feature.editor.event.subscribeAllEvents
import io.github.rosemoe.sora.event.Event
import io.github.rosemoe.sora.event.InterceptTarget
import io.github.rosemoe.sora.event.Unsubscribe
import io.github.rosemoe.sora.widget.base.EditorPopupWindow
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class BaseEditorPopup(
    editor: BaseEditor,
    features: Int
) : EditorPopupWindow(editor, features), EditorEventAware {

    protected val scope = CoroutineScope(Dispatchers.Main + SupervisorJob()) +
            CoroutineName("EditorPopupScope")

    private val eventManager by lazy { editor.createSubEventManager() }

    private var touchOffset = Offset(0f, 0f)

    abstract val rootView: View
    abstract val size: Size

    init {
        scope.launch {
            setContentView(rootView)
            setSize(size.width, size.height)
            popup.animationStyle = io.github.rosemoe.sora.R.style.text_action_popup_animation

            subscribeAllEvents(eventManager)
        }
    }

    final override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
    }

    final override fun getWidth(): Int {
        return size.width
    }

    final override fun getHeight(): Int {
        return size.height
    }

    override fun onEditorTouch(event: EditorTouchEvent, unsubscribe: Unsubscribe) {
        super.onEditorTouch(event, unsubscribe)
        touchOffset = Offset(event.x, event.y)
    }

    override fun show() {
        show(touchOffset)
    }

    override fun dismiss() {
        dismiss(null)
    }

    open fun show(offset: Offset) {
        editor.postInLifecycle {
            setLocationAbsolutely(offset.x.toInt(), offset.y.toInt())
            super.show()
        }
    }

    fun dismiss(event: Event?) {
        if (isShowing) {
            super.dismiss()
            // Not all events can be intercepted
            runSafe {
                event?.intercept(InterceptTarget.TARGET_EDITOR)
            }
        }
    }

}