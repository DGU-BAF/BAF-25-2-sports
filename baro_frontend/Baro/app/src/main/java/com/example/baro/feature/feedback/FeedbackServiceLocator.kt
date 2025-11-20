// feature/feedback/FeedbackServiceLocator.kt
package com.example.baro.feature.feedback

import com.example.baro.feature.feedback.data.api.FeedbackApi
import com.example.baro.feature.feedback.data.repository.FeedbackRepositoryImpl
import com.example.baro.feature.feedback.data.source.FeedbackRemoteDataSourceImpl
import com.example.baro.feature.feedback.domain.repository.FeedbackRepository
import com.example.baro.feature.feedback.domain.usecase.GetFeedbackTargetsUseCase
import com.example.baro.feature.feedback.domain.usecase.GetMyPartiesForFeedbackUseCase
import com.example.baro.feature.feedback.domain.usecase.SubmitFeedbackUseCase
import retrofit2.Retrofit

object FeedbackServiceLocator {

    private lateinit var repository: FeedbackRepository

    lateinit var getMyPartiesForFeedbackUseCase: GetMyPartiesForFeedbackUseCase
        private set

    lateinit var getFeedbackTargetsUseCase: GetFeedbackTargetsUseCase
        private set

    lateinit var submitFeedbackUseCase: SubmitFeedbackUseCase
        private set

    fun init(retrofit: Retrofit) {
        val api = retrofit.create(FeedbackApi::class.java)
        val remote = FeedbackRemoteDataSourceImpl(api)
        repository = FeedbackRepositoryImpl(remote)

        getMyPartiesForFeedbackUseCase = GetMyPartiesForFeedbackUseCase(repository)
        getFeedbackTargetsUseCase = GetFeedbackTargetsUseCase(repository)
        submitFeedbackUseCase = SubmitFeedbackUseCase(repository)
    }
}
