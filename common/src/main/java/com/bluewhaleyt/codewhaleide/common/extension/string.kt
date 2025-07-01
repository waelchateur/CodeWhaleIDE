@file:JvmMultifileClass
@file:JvmName("StringUtilsKt")

package com.bluewhaleyt.codewhaleide.common.extension

fun String.isCJK(): Boolean {
    for (element in this) {
        val ch = element
        val block = Character.UnicodeBlock.of(ch)
        if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS == block ||
            Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS == block ||
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A == block
        ) {
            return true
        }
    }
    return false
}