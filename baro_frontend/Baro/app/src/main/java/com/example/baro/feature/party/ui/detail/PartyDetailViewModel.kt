package com.example.baro.feature.party.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.party.PartyServiceLocator
import com.example.baro.feature.party.domain.model.PartyDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PartyDetailViewModel : ViewModel() {

    private val getPartyDetailUseCase = PartyServiceLocator.getPartyDetailUseCase
    private val joinPartyUseCase = PartyServiceLocator.joinPartyUseCase
    private val leavePartyUseCase = PartyServiceLocator.leavePartyUseCase

    private val _partyDetail = MutableStateFlow<PartyDetail?>(null)
    val partyDetail: StateFlow<PartyDetail?> = _partyDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadPartyDetail(partyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val detail = getPartyDetailUseCase(partyId)
                _partyDetail.value = detail
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onClickActionButton() {
        val current = _partyDetail.value ?: return

        if (!current.isScheduled) return  // 진행 중/완료/취소면 아무것도 안 함

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val updated = if (current.isJoined) {
                    // 나가기
                    leavePartyUseCase(current.partyId)
                } else {
                    // 참여하기
                    if (current.isFull) {
                        _errorMessage.value = "인원이 가득 찼습니다."
                        _isLoading.value = false
                        return@launch
                    }
                    joinPartyUseCase(current.partyId)
                }
                _partyDetail.value = updated
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun canShowActionButton(): Boolean {
        val current = _partyDetail.value ?: return false
        // 방장은 나가기 버튼 없음 → 액션 버튼 자체 숨김
        if (current.isHost) return false
        // 예정 상태에서만 버튼 노출
        return current.isScheduled
    }

    fun getActionButtonText(): String {
        val current = _partyDetail.value ?: return "참여하기"
        return if (current.isJoined) "나가기" else "참여하기"
    }

    fun isActionButtonEnabled(): Boolean {
        val current = _partyDetail.value ?: return false
        if (!current.isScheduled) return false
        if (current.isJoined) return true        // 이미 참여 중이면 나가기는 가능
        return !current.isFull                   // 참여하기는 자리가 있을 때만
    }
}
