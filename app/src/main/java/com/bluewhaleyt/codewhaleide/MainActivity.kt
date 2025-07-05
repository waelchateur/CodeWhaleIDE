package com.bluewhaleyt.codewhaleide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bluewhaleyt.codewhaleide.common.ui.SystemBarsVisibility
import com.bluewhaleyt.codewhaleide.common.ui.Theme
import com.bluewhaleyt.codewhaleide.common.ui.ThemeProperties
import com.bluewhaleyt.codewhaleide.feature.editor.Editor

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.safeDrawingPadding().imePadding()) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { Editor(it) }
                        )
                    }
                }
            }
        }
    }

}