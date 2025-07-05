package com.bluewhaleyt.codewhaleide.common.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bluewhaleyt.codewhaleide.common.extension.density
import com.bluewhaleyt.codewhaleide.common.extension.findActivity
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.rememberDynamicThemeState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(
    colorScheme: ColorScheme? = null,
    motionScheme: MotionScheme = MaterialTheme.motionScheme,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    properties: ThemeProperties = ThemeProperties(),
    content: @Composable () -> Unit
) {
    with(properties) {
        val context = LocalContext.current
        val view = LocalView.current
        val configuration = LocalConfiguration.current

        val colorTuple = if (mode is Mode.Monet) mode.colorTuple else
            ColorTuple(Color.Unspecified)

        val paletteStyle = with(PaletteStyle.TonalSpot) {
            if (mode is Mode.Monet) mode.paletteStyle ?: this else this
        }

        val contrastLevel = if (mode is Mode.Monet) mode.contrastLevel else 0f

        val darkTheme = nightMode.isDark

        val amoled = useAmoled.takeIf { darkTheme } ?: false

        DynamicTheme(
            state = rememberDynamicThemeState(colorTuple),
            isDarkTheme = darkTheme,
            defaultColorTuple = colorTuple,
            typography = typography,
            density = context.density,
            dynamicColor = mode.isDynamicColor,
            amoledMode = amoled,
            isInvertColors = useInvertedColors,
            style = paletteStyle,
            contrastLevel = contrastLevel.toDouble()
        ) {
            val newColorScheme = when (mode) {
                is Mode.Baseline -> if (darkTheme) darkColorScheme() else lightColorScheme()
                else -> colorScheme
            } ?: MaterialTheme.colorScheme

            BaseTheme(
                colorScheme = newColorScheme,
                motionScheme = motionScheme,
                typography = typography,
                shapes = shapes,
                useExpressive = useExpressive
            ) {
                content()
            }

            if (!view.isInEditMode) {
                @Suppress("DEPRECATION")
                SideEffect {
                    val window = context.findActivity()?.window!!
                    val windowInsetsController = WindowInsetsControllerCompat(window, view)
                    systemBarsVisibility.handle(configuration, windowInsetsController)
                    windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                    windowInsetsController.isAppearanceLightStatusBars = !darkTheme || useInvertedColors
                    windowInsetsController.isAppearanceLightNavigationBars = !darkTheme || useInvertedColors

                    window.statusBarColor = (if (systemBarsVisibility.isStatusBarVisible(configuration)) {
                        newColorScheme.surface
                    } else Color.Transparent).toArgb()
                    window.navigationBarColor = (if (systemBarsVisibility.isNavigationBarVisible(configuration)) {
                        newColorScheme.surface
                    } else Color.Transparent).toArgb()

                    AppCompatDelegate.setDefaultNightMode(nightMode.mode)
                }

                LaunchedEffect(nightMode) {
                    AppCompatDelegate.setDefaultNightMode(nightMode.mode)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BaseTheme(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    motionScheme: MotionScheme = MaterialTheme.motionScheme,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    useExpressive: Boolean = false,
    content: @Composable () -> Unit
) {
    if (useExpressive) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = motionScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    } else {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

@Immutable
data class ThemeProperties(
    val mode: Mode = Mode.Auto,
    val nightMode: NightMode = NightMode.System,
    val systemBarsVisibility: SystemBarsVisibility = SystemBarsVisibility.Auto,
    val useExpressive: Boolean = true,
    val useAmoled: Boolean = false,
    val useInvertedColors: Boolean = false
)

sealed interface Mode {

    val isDynamicColor
        get() = when (this) {
            is Auto -> supportsDynamicColor
            is Baseline -> false
            is DynamicColor -> true
            is Monet -> false
        }

    data object Auto : Mode

    data class Monet(
        val colorTuple: ColorTuple,
        val paletteStyle: PaletteStyle? = null,
        @FloatRange(from = 0.0, to = 1.0) val contrastLevel: Float = 0f
    ) : Mode

    data object DynamicColor : Mode

    data object Baseline : Mode

    companion object {
        val entries
            get() = Mode::class.sealedSubclasses.map { it.objectInstance }
    }
}

sealed interface NightMode {

    val mode: Int

    val isDark: Boolean
        @SuppressLint("ComposableNaming")
        @Composable get() = when (this) {
            is System -> isSystemInDarkTheme()
            is Light -> false
            is Dark -> true
        }

    data object System : NightMode {
        override val mode: Int
            get() = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    data object Dark : NightMode {
        override val mode: Int
            get() = AppCompatDelegate.MODE_NIGHT_YES
    }

    data object Light : NightMode {
        override val mode: Int
            get() = AppCompatDelegate.MODE_NIGHT_NO
    }

    companion object {
        val entries
            get() = NightMode::class.sealedSubclasses.map { it.objectInstance }
    }

}

sealed interface SystemBarsVisibility {

    val isStatusBarVisible: (Configuration) -> Boolean
        get() = {
            this !is HideAll && this !is ShowNavigationBarOnly
        }

    val isNavigationBarVisible: (Configuration) -> Boolean
        get() = {
            when (this) {
                is Auto -> it.orientation != Configuration.ORIENTATION_LANDSCAPE
                is ShowAll -> true
                is HideAll -> false
                is ShowStatusBarOnly -> false
                is ShowNavigationBarOnly -> true
            }
        }

    fun handle(
        configuration: Configuration,
        controller: WindowInsetsControllerCompat
    )

    data object Auto : SystemBarsVisibility {
        override fun handle(
            configuration: Configuration,
            controller: WindowInsetsControllerCompat
        ) {
            controller.setVisible(TYPE_STATUS_BAR, true)
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                controller.setVisible(TYPE_NAVIGATION_BAR, false)
            } else controller.setVisible(TYPE_NAVIGATION_BAR, true)
        }
    }

    data object HideAll : SystemBarsVisibility {
        override fun handle(
            configuration: Configuration,
            controller: WindowInsetsControllerCompat
        ) {
            controller.setVisible(TYPE_SYSTEM_BARS, false)
        }
    }

    data object ShowAll : SystemBarsVisibility {
        override fun handle(
            configuration: Configuration,
            controller: WindowInsetsControllerCompat
        ) {
            controller.setVisible(TYPE_SYSTEM_BARS, true)
        }
    }

    data object ShowStatusBarOnly : SystemBarsVisibility {
        override fun handle(
            configuration: Configuration,
            controller: WindowInsetsControllerCompat
        ) {
            controller.setVisible(TYPE_STATUS_BAR, true)
            controller.setVisible(TYPE_NAVIGATION_BAR, false)
        }
    }

    data object ShowNavigationBarOnly : SystemBarsVisibility {
        override fun handle(
            configuration: Configuration,
            controller: WindowInsetsControllerCompat
        ) {
            controller.setVisible(TYPE_NAVIGATION_BAR, true)
            controller.setVisible(TYPE_STATUS_BAR, false)
        }
    }

    companion object {
        val entries
            get() = SystemBarsVisibility::class.sealedSubclasses.map { it.objectInstance }

        private val TYPE_STATUS_BAR = WindowInsetsCompat.Type.statusBars()
        private val TYPE_NAVIGATION_BAR = WindowInsetsCompat.Type.navigationBars()
        private val TYPE_SYSTEM_BARS = WindowInsetsCompat.Type.systemBars()
    }

}

private fun WindowInsetsControllerCompat.setVisible(types: Int, visible: Boolean) = run {
    if (visible) {
        show(types)
    } else {
        hide(types)
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@ChecksSdkIntAtLeast(Build.VERSION_CODES.S)
private val supportsDynamicColor =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S