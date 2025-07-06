package com.bluewhaleyt.codewhaleide.feature.editor.popup

import android.content.Context
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bluewhaleyt.codewhaleide.common.extension.findActivity
import com.bluewhaleyt.codewhaleide.common.ui.Theme
import com.bluewhaleyt.codewhaleide.feature.editor.BaseEditor
import io.github.rosemoe.sora.widget.base.EditorPopupWindow.FEATURE_HIDE_WHEN_FAST_SCROLL
import io.github.rosemoe.sora.widget.base.EditorPopupWindow.FEATURE_SCROLL_AS_CONTENT
import io.github.rosemoe.sora.widget.base.EditorPopupWindow.FEATURE_SHOW_OUTSIDE_VIEW_ALLOWED

abstract class EditorPopup(
    editor: BaseEditor,
    open val type: Type
) : BaseEditorPopup(editor, getFeaturesByType(type)) {

    private var composeView: ComposeView
    private var frameLayout: FrameLayout

    @Composable
    abstract fun Content()

    // Do not need to specify the size of the window when using Compose layout
    final override val size: Size
        get() = with(rootView.layoutParams) {
            Size(width, height)
        }

    final override val rootView: View
        get() = frameLayout

    init {
        composeView = ComposeView(editor.context).apply {
            setContent {
                @OptIn(ExperimentalMaterial3ExpressiveApi::class)
                Theme {
                    this@EditorPopup.Content()
                }
            }
        }
        frameLayout = composeView.asFrameLayout(editor.context)
    }

    override fun show() {
        when (type) {
            Type.Bound -> show(Position.TOP_LEFT)
            Type.Content -> show(editor.cursor.leftLine, editor.cursor.leftColumn)
        }
    }

    open fun show(position: Position) {
        require(type == Type.Bound)
        show(calculateOffsetByPosition(position))
    }

    open fun show(line: Int, column: Int, stickToLine: Boolean = false) {
        require(type == Type.Content)
        val charX = editor.getCharOffsetX(line, column)
        val charY = editor.getCharOffsetY(line, column)
        val y = if (stickToLine) charY else charY + editor.rowHeight
        val offset = Offset(charX, y)
        show(offset)
    }

    private fun calculateOffsetByPosition(position: Position): Offset {
        return when (position) {
            Position.TOP_LEFT -> Offset(0f, 0f)
            Position.TOP_RIGHT -> Offset((editor.width - width).toFloat(), 0f)
            Position.BOTTOM_LEFT -> Offset(0f, (editor.height - height).toFloat())
            Position.BOTTOM_RIGHT -> Offset((editor.height - height).toFloat(), (editor.height - height).toFloat())
        }
    }


    enum class Position {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    enum class Type {
        Bound,
        Content
    }

}

private fun getFeaturesByType(type: EditorPopup.Type) = when (type) {
    EditorPopup.Type.Bound -> FEATURE_SHOW_OUTSIDE_VIEW_ALLOWED or FEATURE_SCROLL_AS_CONTENT
    EditorPopup.Type.Content -> FEATURE_SHOW_OUTSIDE_VIEW_ALLOWED or FEATURE_HIDE_WHEN_FAST_SCROLL
}

private fun ComposeView.asFrameLayout(
    context: Context
) = with (context.findActivity()) {
    FrameLayout(context).apply {
        id = android.R.id.content
        setViewTreeLifecycleOwner(this@with)
        setViewTreeSavedStateRegistryOwner(this@with)
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addView(this@asFrameLayout)
    }
}