package com.medassist.app.utils

import android.content.Context
import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for PreferenceManager class
 */
@RunWith(MockitoJUnitRunner::class)
class PreferenceManagerTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var preferenceManager: PreferenceManager

    @Before
    fun setUp() {
        `when`(mockContext.getSharedPreferences("MedAssistPrefs", Context.MODE_PRIVATE))
            .thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.remove(anyString())).thenReturn(mockEditor)
        
        preferenceManager = PreferenceManager(mockContext)
    }

    @Test
    fun `setUserLoggedIn should save login state correctly`() {
        // When
        preferenceManager.setUserLoggedIn(true)

        // Then
        verify(mockEditor).putBoolean("is_logged_in", true)
        verify(mockEditor).apply()
    }

    @Test
    fun `isUserLoggedIn should return correct login state`() {
        // Given
        `when`(mockSharedPreferences.getBoolean("is_logged_in", false)).thenReturn(true)

        // When
        val result = preferenceManager.isUserLoggedIn()

        // Then
        assert(result == true)
    }

    @Test
    fun `setUserName should save user name correctly`() {
        // Given
        val userName = "Test User"

        // When
        preferenceManager.setUserName(userName)

        // Then
        verify(mockEditor).putString("user_name", userName)
        verify(mockEditor).apply()
    }

    @Test
    fun `getUserName should return correct user name`() {
        // Given
        val expectedName = "Test User"
        `when`(mockSharedPreferences.getString("user_name", "Guest")).thenReturn(expectedName)

        // When
        val result = preferenceManager.getUserName()

        // Then
        assert(result == expectedName)
    }

    @Test
    fun `setDarkThemeEnabled should save theme preference correctly`() {
        // When
        preferenceManager.setDarkThemeEnabled(true)

        // Then
        verify(mockEditor).putBoolean("dark_theme", true)
        verify(mockEditor).apply()
    }

    @Test
    fun `logout should clear user data`() {
        // When
        preferenceManager.logout()

        // Then
        verify(mockEditor).putBoolean("is_logged_in", false)
        verify(mockEditor).remove("user_name")
        verify(mockEditor).remove("user_email")
        verify(mockEditor, times(3)).apply()
    }
}

