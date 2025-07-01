@file:JvmMultifileClass
@file:JvmName("ContextUtilsKt")

package com.bluewhaleyt.codewhaleide.common.extension

import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Density
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

internal val Context.density: Density
    get() = object : Density {
        override val density: Float
            get() = resources.displayMetrics.density
        override val fontScale: Float
            get() = resources.configuration.fontScale
    }

tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}