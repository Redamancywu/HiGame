package com.higame.sdk.core.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtils {
    private const val PREF_NAME = "hi_game_sdk_pref"
    private const val KEY_GUEST_ID = "guest_id"
    private const val KEY_TOKEN = "token"
    private const val KEY_GAME_ACCOUNT_ID = "game_account_id"
    private var preferences: SharedPreferences? = null

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveGuestId(guestId: String) {
        preferences?.edit()?.putString(KEY_GUEST_ID, guestId)?.apply()
    }

    fun getGuestId(): String? {
        return preferences?.getString(KEY_GUEST_ID, null)
    }

    fun generateGuestId(): String {
        return "guest_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    fun generateToken(): String {
        return "token_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    fun generateGameAccountId(): String {
        return "account_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    fun saveToken(token: String) {
        preferences?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }

    fun getToken(): String? {
        return preferences?.getString(KEY_TOKEN, null)
    }

    fun saveGameAccountId(accountId: String) {
        preferences?.edit()?.putString(KEY_GAME_ACCOUNT_ID, accountId)?.apply()
    }

    fun getGameAccountId(): String? {
        return preferences?.getString(KEY_GAME_ACCOUNT_ID, null)
    }
}