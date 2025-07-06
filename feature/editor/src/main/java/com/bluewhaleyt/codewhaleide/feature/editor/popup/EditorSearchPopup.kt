package com.bluewhaleyt.codewhaleide.feature.editor.popup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bluewhaleyt.codewhaleide.feature.editor.BaseEditor
import com.bluewhaleyt.codewhaleide.feature.editor.event.EditorSearchEvent
import io.github.rosemoe.sora.event.Unsubscribe

class EditorSearchPopup(
    private val editor: BaseEditor
) : EditorPopup(editor, Type.Bound) {

    private var searchText by mutableStateOf<String?>(null)
    private var replaceText by mutableStateOf<String?>(null)

    init {
        popup.isFocusable = true
    }

    @Composable
    override fun Content() {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchText ?: "",
                    onValueChange = { searchText = it },
                    label = { Text("Search") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = replaceText ?: "",
                    onValueChange = { replaceText = it },
                    label = { Text("Replace") }
                )

                Row {
                    Button(
                        enabled = !searchText.isNullOrBlank() && !replaceText.isNullOrBlank(),
                        onClick = {
                            replaceText?.let {
                                editor.searcher.replace(it, false)
                            }
                        }
                    ) { Text("Replace")  }

                    Button(
                        enabled = !searchText.isNullOrBlank() && !replaceText.isNullOrBlank(),
                        onClick = {
                            replaceText?.let {
                                editor.searcher.replace(it, true)
                            }
                        }
                    ) { Text("Replace All")  }
                }

                LaunchedEffect(searchText) {
                    searchText?.let {
                        editor.searcher.search(it, caseSensitive = true)
                    }
                }
            }
        }
    }

    override fun onEditorSearchChange(event: EditorSearchEvent, unsubscribe: Unsubscribe) {
        super.onEditorSearchChange(event, unsubscribe)
        if (event.editor.searcher.hasQuery()) {
            show(Position.TOP_RIGHT)
            searchText = event.query
        }
    }

}