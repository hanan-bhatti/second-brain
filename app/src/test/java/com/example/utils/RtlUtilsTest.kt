package com.example.utils

import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RtlUtilsTest {

    @Test
    fun isRtlText_englishText_returnsFalse() {
        assertFalse(isRtlText("Hello world"))
    }

    @Test
    fun isRtlText_arabicText_returnsTrue() {
        assertTrue(isRtlText("مرحبا بالعالم"))
    }

    @Test
    fun isRtlText_urduText_returnsTrue() {
        assertTrue(isRtlText("ہیلو دنیا"))
    }

    @Test
    fun isRtlText_hebrewText_returnsTrue() {
        assertTrue(isRtlText("שלום עולם"))
    }

    @Test
    fun isRtlText_mixedTextStartingWithEnglish_returnsFalse() {
        assertFalse(isRtlText("Hello مرحبا"))
    }

    @Test
    fun isRtlText_mixedTextStartingWithArabic_returnsTrue() {
        assertTrue(isRtlText("مرحبا Hello"))
    }

    @Test
    fun isRtlText_textWithLeadingWhitespace_ignoresWhitespace() {
        assertTrue(isRtlText("   مرحبا"))
        assertFalse(isRtlText("   Hello"))
    }

    @Test
    fun isRtlText_emptyString_returnsFalse() {
        assertFalse(isRtlText(""))
    }

    @Test
    fun isRtlText_numbersOnly_returnsFalse() {
        assertFalse(isRtlText("12345"))
    }

    @Test
    fun isRtlText_specialCharactersOnly_returnsFalse() {
        assertFalse(isRtlText("!@#$%^&*()_+"))
    }

    @Test
    fun getLayoutDirectionForText_rtlText_returnsRtl() {
        assertEquals(LayoutDirection.Rtl, getLayoutDirectionForText("مرحبا بالعالم"))
    }

    @Test
    fun getLayoutDirectionForText_ltrText_returnsLtr() {
        assertEquals(LayoutDirection.Ltr, getLayoutDirectionForText("Hello world"))
    }

    @Test
    fun getLayoutDirectionForText_emptyText_returnsLtr() {
        assertEquals(LayoutDirection.Ltr, getLayoutDirectionForText(""))
    }
}
