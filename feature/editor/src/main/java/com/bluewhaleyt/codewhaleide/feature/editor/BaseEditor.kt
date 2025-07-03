package com.bluewhaleyt.codewhaleide.feature.editor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import com.bluewhaleyt.codewhaleide.feature.editor.utils.EditorController
import com.bluewhaleyt.codewhaleide.feature.editor.utils.EditorSearchController
import io.github.rosemoe.sora.event.ColorSchemeUpdateEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.event.subscribeAlways
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.EditorContextMenuCreator
import io.github.rosemoe.sora.widget.component.EditorDiagnosticTooltipWindow
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow
import io.github.rosemoe.sora.widget.getComponent
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

@SuppressLint("ViewConstructor")
abstract class BaseEditor @JvmOverloads internal constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : CodeEditor(context, attrs, defStyleAttr, defStyleRes) {

    protected val scope = CoroutineScope(Dispatchers.Main + SupervisorJob()) +
            CoroutineName("EditorScope")

    protected val controller by lazy { EditorController(this) }
    protected val searchController by lazy { EditorSearchController(this) }

    private val eventManager by lazy { createSubEventManager() }

    val selectedText
        get() = controller.getSelectedText()

    var selectionHighlightEnabled = true

    init {
        scope.launch {
            with(Typeface.createFromAsset(context.assets, "editor/font/iosevka.ttc")) {
                typefaceText = this
                typefaceLineNumber = this
            }
            lineNumberMarginLeft = 40f
            setDividerMargin(40f, 0f)
            colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
            props.apply {
                enableRoundTextBackground = false
            }
            updateColorScheme(colorScheme)
            subscribeEvents()
        }
    }

    override fun hideEditorWindows() {
        super.hideEditorWindows()

    }

    override fun copyText() {
        if (text.isNotBlank()) super.copyText()
    }

    override fun pasteText() {
        if (text.isNotBlank()) super.pasteText()
    }

    override fun undo() {
        if (canUndo()) super.undo()
    }

    override fun redo() {
        if (canRedo()) super.redo()
    }

    private fun subscribeEvents() {
        eventManager.subscribeAlways(::onColorSchemeUpdate)
        eventManager.subscribeAlways(::onSelectionChange)
    }

    private fun onColorSchemeUpdate(event: ColorSchemeUpdateEvent) {
        updateColorScheme(event.colorScheme)
    }

    private fun onSelectionChange(event: SelectionChangeEvent) {
        if (selectionHighlightEnabled) {
            scope.launch {
                searchController.highlightSelection(event)
            }
        }
    }

    private fun enableBuiltInComponents(enabled: Boolean) {
        getComponent<EditorTextActionWindow>().isEnabled = enabled
        getComponent<EditorAutoCompletion>().isEnabled = enabled
        getComponent<EditorDiagnosticTooltipWindow>().isEnabled = enabled
        getComponent<EditorContextMenuCreator>().isEnabled = enabled
    }

    private fun updateColorScheme(colorScheme: EditorColorScheme) {
        fun color(type: Int, alpha: Int = 255) = run {
            val isDark = ColorUtils.calculateLuminance(
                colorScheme.getColor(EditorColorScheme.WHOLE_BACKGROUND)
            ) < 0.4f
            val newAlpha = if (isDark || alpha == 255) alpha else alpha / 2
            ColorUtils.setAlphaComponent(colorScheme.getColor(type), newAlpha)
        }

        with(EditorColorScheme.TEXT_NORMAL) {
            colorScheme.setColor(EditorColorScheme.MATCHED_TEXT_BACKGROUND, color(this, 40))
            colorScheme.setColor(EditorColorScheme.SNIPPET_BACKGROUND_EDITING, color(this, 40))
            colorScheme.setColor(EditorColorScheme.SNIPPET_BACKGROUND_RELATED, color(this, 20))
            colorScheme.setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_FOREGROUND, color(this))
            colorScheme.setColor(EditorColorScheme.SELECTION_INSERT, color(this))
            colorScheme.setColor(EditorColorScheme.SELECTION_HANDLE, color(this))
        }

        colorScheme.setColor(
            EditorColorScheme.HIGHLIGHTED_DELIMITERS_BACKGROUND, color(
                EditorColorScheme.BLOCK_LINE_CURRENT, 100))
        colorScheme.setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_UNDERLINE, Color.TRANSPARENT)
    }

}