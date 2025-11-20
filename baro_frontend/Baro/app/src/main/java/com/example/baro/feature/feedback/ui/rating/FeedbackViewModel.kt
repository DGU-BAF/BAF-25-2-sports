// feature/feedback/ui/rating/FeedbackViewModel.kt
package com.example.baro.feature.feedback.ui.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.feedback.FeedbackServiceLocator
import com.example.baro.feature.feedback.domain.model.MemberRatingInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel : ViewModel() {

    private val getTargetsUseCase = FeedbackServiceLocator.getFeedbackTargetsUseCase
    private val submitFeedbackUseCase = FeedbackServiceLocator.submitFeedbackUseCase

    private val _members = MutableStateFlow<List<RatingMemberUiModel>>(emptyList())
    val members: StateFlow<List<RatingMemberUiModel>> = _members

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _submitSuccess = MutableStateFlow(false)
    val submitSuccess: StateFlow<Boolean> = _submitSuccess

    private var partyId: String? = null

    fun loadTargets(partyId: String) {
        if (this.partyId == partyId && _members.value.isNotEmpty()) return
        this.partyId = partyId

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val domains = getTargetsUseCase(partyId)
                _members.value = domains.map { RatingMemberUiModel.from(it) }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateRating(userId: String, rating: Int) {
        _members.value = _members.value.map {
            if (it.userId == userId) it.copy(rating = rating) else it
        }
    }

    fun submit() {
        val pid = partyId ?: return
        val ratings = _members.value.map {
            MemberRatingInput(
                userId = it.userId,
                rating = it.rating
            )
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                submitFeedbackUseCase(pid, ratings)
                _submitSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun consumeSubmitSuccess() {
        _submitSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
