// app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // ✅ Kotlinx Serialization 플러그인 (중요)
    // libs.versions.toml 에서
    // kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
    alias(libs.plugins.kotlin.serialization)

    // Navigation Safe Args
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.example.baro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.baro"
        minSdk = 24
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX & Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.swiperefreshlayout)

    // Kakao SDK

    implementation("com.kakao.sdk:v2-user:2.20.1") // 사용자 정보 가져오기
    implementation("com.kakao.sdk:v2-auth:2.20.1") // 카카오 인증 (로그인)

    // Retrofit
    implementation(libs.retrofit)

    // ✅ Retrofit + Kotlinx Serialization 컨버터
    // libs.versions.toml 예시:
    // retrofit-converter-kotlinx = { module = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter", version = "0.8.0" }
    implementation(libs.retrofit.converter.kotlinx)

    // ✅ Kotlinx Serialization JSON
    // libs.versions.toml 예시:
    // kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version = "1.6.3" }
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // GSON (정말 안 쓰면 나중에 제거해도 됨)
    implementation("com.google.code.gson:gson:2.11.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ✅ [핵심 수정] Supabase 라이브러리를 최신 bom:2.4.0 버전에 맞게 수정합니다.
    // 1. Supabase Bill of Materials (BOM) - 버전 관리를 위함
    implementation(platform("io.github.jan-tennert.supabase:bom:2.4.0"))

    // 2. 필요한 Supabase 모듈들 (옛날 이름 auth-kt, postgrest-kt 등은 모두 삭제)
    implementation("io.github.jan-tennert.supabase:gotrue-kt")      // 인증 (Auth)
    implementation("io.github.jan-tennert.supabase:postgrest-kt")   // 데이터베이스 (PostgREST)
    implementation("io.github.jan-tennert.supabase:storage-kt")     // 파일 스토리지
    implementation("io.github.jan-tennert.supabase:realtime-kt")    // 실시간 기능

    // 3. Supabase가 내부적으로 사용하는 Ktor 클라이언트 엔진 (반드시 필요)
    implementation("io.ktor:ktor-client-okhttp:2.3.11")

    // 이 두 줄을 dependencies { ... } 안에 추가하세요.
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-core:1.1.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // 위치
    implementation("com.google.android.gms:play-services-location:21.3.0")
}
