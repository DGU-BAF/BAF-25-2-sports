package com.example.baro.core

import android.content.Context
import android.content.SharedPreferences

object Prefs {

    private const val PREF = "baro_prefs"
    private const val KEY_PROFILE_DONE = "profile_done"
    private const val KEY_KAKAO_ID = "kakao_id"

    fun setProfileDone(ctx: android.content.Context, done: Boolean) =
        ctx.getSharedPreferences(PREF, 0).edit().putBoolean(KEY_PROFILE_DONE, done).apply()
    fun isProfileDone(ctx: android.content.Context) =
        ctx.getSharedPreferences(PREF, 0).getBoolean(KEY_PROFILE_DONE, false)

    fun setKakaoId(ctx: android.content.Context, id: Long) =
        ctx.getSharedPreferences(PREF, 0).edit().putLong(KEY_KAKAO_ID, id).apply()
    fun getKakaoId(ctx: android.content.Context) =
        ctx.getSharedPreferences(PREF, 0).getLong(KEY_KAKAO_ID, -1L)
}
