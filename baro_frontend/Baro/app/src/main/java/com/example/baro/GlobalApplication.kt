package com.example.baro

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.baro.feature.auth.data.local.SessionManager
import com.example.baro.feature.bot.BotServiceLocator
import com.example.baro.feature.feedback.FeedbackServiceLocator
import com.example.baro.feature.party.PartyServiceLocator
import com.kakao.sdk.common.KakaoSdk
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
//import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 이 import 문을 GlobalApplication.kt 파일 상단에 추가하세요.
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

class GlobalApplication : Application() {

    val dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "baro_preferences"
    )

    companion object {
        lateinit var instance: GlobalApplication
            private set

        lateinit var supabase: SupabaseClient
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))

        supabase = createSupabaseClient(
            supabaseUrl = "https://ydqqbjjsriadezcnkbmx.supabase.co",
            supabaseKey = "YOUR_KEY"
        ) {
            install(io.github.jan.supabase.gotrue.Auth)
        }

        // ---------------------------
        // 💡 여기서 Retrofit 생성
        // ---------------------------
        val contentType = "application/json".toMediaType()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://baro-backend.fly.dev/") // FastAPI 서버 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // ---------------------------
        // 💡 PartyServiceLocator 초기화
        // ---------------------------
//        PartyServiceLocator.init(retrofit)

        // GlobalApplication.onCreate() 안
        FeedbackServiceLocator.init(retrofit)
        // [추가]
        val sessionManager = SessionManager(dataStore)
        BotServiceLocator.init(sessionManager)


    }
}
