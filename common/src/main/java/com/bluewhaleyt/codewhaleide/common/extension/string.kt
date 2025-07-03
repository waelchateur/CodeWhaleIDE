@file:JvmMultifileClass
@file:JvmName("StringUtilsKt")

package com.bluewhaleyt.codewhaleide.common.extension

fun Char.isCJK(): Boolean {
    val str = toString()
    var offset = 0
    while (offset < str.length) {
        val codepoint = Character.codePointAt(str, offset)
        val block = Character.UnicodeBlock.of(codepoint)
        if (
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS == block ||
                    Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A == block ||
                    Character.UnicodeBlock.CJK_COMPATIBILITY == block ||
                    Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS == block ||
                    Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS == block ||
                    Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT == block ||
                    Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT == block ||
                    Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION == block ||
                    Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS == block ||
                    Character.UnicodeBlock.KANGXI_RADICALS == block ||
                    Character.UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS == block ||
                    Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B == block ||
                    Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C == block ||
                    Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D == block ||
                    Character.UnicodeBlock.CJK_STROKES == block ||
                    Character.UnicodeBlock.ENCLOSED_IDEOGRAPHIC_SUPPLEMENT == block
        ) return true
        offset += Character.charCount(codepoint)
    }
    return false
}