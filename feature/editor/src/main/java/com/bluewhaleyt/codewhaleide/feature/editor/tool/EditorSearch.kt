package com.bluewhaleyt.codewhaleide.feature.editor.tool

import com.bluewhaleyt.codewhaleide.common.extension.isCJK
import com.bluewhaleyt.codewhaleide.common.extension.runSafe
import com.bluewhaleyt.codewhaleide.feature.editor.BaseEditor
import com.bluewhaleyt.codewhaleide.feature.editor.extension.selectedText
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.widget.EditorSearcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditorSearch internal constructor(
    private val editor: BaseEditor
) {

    private val searcher by lazy { editor.searcher }

    suspend fun highlightSelection(event: SelectionChangeEvent) {
        if (event.isSelected) {
            val word = editor.selectedText
            search(
                query = word.toString(),
                caseSensitive = true
            )
        } else {
            val word = findWord(event.left.line, event.left.column)
            val matches = findWordMatches(word)
            matches?.let {
                if (it.positions.size > 1) {
                    search(
                        query = it.word.filter { it.isLetter() || it.isCJK() },
                        caseSensitive = true,
                        type = EditorSearcher.SearchOptions.TYPE_WHOLE_WORD
                    )
                } else searcher.stopSearch()
            } ?: searcher.stopSearch()
        }
    }

    fun search(
        query: String,
        caseSensitive: Boolean = false,
        singleLine: Boolean = true,
        type: Int = EditorSearcher.SearchOptions.TYPE_NORMAL
    ) {
        runSafe {
            if (query.isNotBlank()) {
                val newQuery = if (singleLine) {
                    query.filter { c -> c != '\n' && c != '\r' }
                } else query

                searcher.search(
                    newQuery,
                    EditorSearcher.SearchOptions(type, !caseSensitive)
                )
            }
        }
    }

    fun replace(
        replacement: String,
        replaceAll: Boolean
    ) {
        runSafe {
            if (replacement.isNotBlank()) {
                if (replaceAll) searcher.replaceAll(replacement)
                else searcher.replaceCurrentMatch(replacement)
            }
        }
    }

    fun terminate() {
        runSafe {
            if (searcher.hasQuery()) {
                searcher.stopSearch()
            }
        }
    }

    private suspend fun findWordMatches(targetWord: String?) = withContext(Dispatchers.IO) {
        if (targetWord == null) return@withContext null

        val lines = editor.text.split("\n")
        val positions = mutableListOf<CharPosition>()

        lines.forEachIndexed { lineIndex, currentLine ->
            var startIndex = 0
            while (startIndex < currentLine.length) {
                val (wordStart, wordEnd) = findWordBoundaries(currentLine, startIndex)
                if (wordStart != -1 && wordEnd != -1 && wordStart <= wordEnd && wordStart < currentLine.length) {
                    val word = currentLine.substring(wordStart, wordEnd)
                    if (word == targetWord) {
                        positions.add(CharPosition(lineIndex, wordStart))
                    }
                    startIndex = wordEnd + 1
                } else break
            }
        }

        return@withContext WordMatches(targetWord, positions)
    }

    private fun findWord(line: Int, column: Int): String? {
        val lines = editor.text.split("\n")
        if (line < 0 || line >= lines.size) return null
        val currentLine = lines[line]
        if (currentLine.isEmpty()) return null

        val (start, end) = findWordBoundaries(currentLine, column)

        return if (start != -1 && end != -1 && start <= end && start < currentLine.length) {
            currentLine.substring(start, end)
        } else null
    }

    private fun findWordBoundaries(line: String, column: Int) =
        findWordStartIndex(line, column) to findWordEndIndex(line, column)

    private fun findWordStartIndex(line: String, column: Int): Int {
        var start = minOf(column, line.length)
        while (start > 0 && line[start - 1].isLetterOrDigit()) {
            start--
        }
        return start
    }

    private fun findWordEndIndex(line: String, column: Int): Int {
        var end = minOf(column, line.length)
        while (end < line.length && line[end].isLetterOrDigit()) {
            end++
        }
        return end
    }

}

data class WordMatches internal constructor(
    val word: String,
    val positions: List<CharPosition>
)