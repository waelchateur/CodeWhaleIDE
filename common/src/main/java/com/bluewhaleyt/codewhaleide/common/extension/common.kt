@file:JvmMultifileClass
@file:JvmName("CommonUtilsKt")

package com.bluewhaleyt.codewhaleide.common.extension

@JvmSynthetic
inline fun runSafe(block: () -> Unit) =
    try { block() } catch (_: Exception) { null }