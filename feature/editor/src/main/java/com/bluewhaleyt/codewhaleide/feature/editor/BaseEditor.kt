package com.bluewhaleyt.codewhaleide.feature.editor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import com.bluewhaleyt.codewhaleide.feature.editor.tool.EditorSearch
import com.bluewhaleyt.codewhaleide.feature.editor.helper.EditorHelper
import io.github.rosemoe.sora.event.ClickEvent
import io.github.rosemoe.sora.event.ColorSchemeUpdateEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.event.Unsubscribe
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.EditorDiagnosticTooltipWindow
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow
import io.github.rosemoe.sora.widget.getComponent
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import io.github.rosemoe.sora.widget.subscribeEvent
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
) : CodeEditor(context, attrs, defStyleAttr, defStyleRes), EditorEventAware {

    protected val scope = CoroutineScope(Dispatchers.Main + SupervisorJob()) +
            CoroutineName("EditorScope")

    protected val helper by lazy { EditorHelper(this) }
    protected val searcher by lazy { EditorSearch(this) }

    var selectionHighlightEnabled = true

    init {
        scope.launch {
            subscribeAllEvents()
            setDividerMargin(40f, 0f)

            colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
            lineNumberMarginLeft = 40f

            helper.setTypeface("editor/font/iosevka.ttc")
            props.apply {
                enableRoundTextBackground = false
                boldMatchingDelimiters = false
            }
        }
    }

    override fun onSelectionChange(event: SelectionChangeEvent, unsubscribe: Unsubscribe) {
        super.onSelectionChange(event, unsubscribe)
        if (selectionHighlightEnabled) {
            scope.launch {
                searcher.highlightSelection(event)
            }
        }
    }

    override fun onColorSchemeChange(event: ColorSchemeUpdateEvent, unsubscribe: Unsubscribe) {
        super.onColorSchemeChange(event, unsubscribe)
        updateColorScheme(event.colorScheme)
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

    private fun subscribeAllEvents() {
        subscribeEvent(::onClick)
        subscribeEvent(::onDoubleClick)
        subscribeEvent(::onLongClick)
        subscribeEvent(::onScrollChange)
        subscribeEvent(::onSelectionChange)
        subscribeEvent(::onContentChange)
        subscribeEvent(::onTextSizeChange)
        subscribeEvent(::onSnippetStateChange)
        subscribeEvent(::onSelectionHandleStateChange)
        subscribeEvent(::onSearchResultChange)
        subscribeEvent(::onColorSchemeChange)
        subscribeEvent(::onKeyPress)
    }

    private fun disableAllBuiltInPopups() {
        getComponent<EditorTextActionWindow>().isEnabled = false
        getComponent<EditorAutoCompletion>().isEnabled = false
        getComponent<EditorDiagnosticTooltipWindow>().isEnabled = false
    }

}