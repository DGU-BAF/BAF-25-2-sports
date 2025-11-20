package com.example.baro.feature.party.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.party.PartyServiceLocator
import com.example.baro.feature.party.domain.model.PartySummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PartyListViewModel : ViewModel() {

    private val getPartyListUseCase = PartyServiceLocator.getPartyListUseCase
    private val getUpcomingJoinedPartiesUseCase = PartyServiceLocator.getUpcomingJoinedPartiesUseCase

    // 원본 서버 데이터
    private val _partyList = MutableStateFlow<List<PartySummary>>(emptyList())
    val partyList: StateFlow<List<PartySummary>> = _partyList

    private val _upcomingJoinedParties = MutableStateFlow<List<PartySummary>>(emptyList())
    val upcomingJoinedParties: StateFlow<List<PartySummary>> = _upcomingJoinedParties

    // UI에서 바로 사용하는 형태
    private val _listItems = MutableStateFlow<List<PartyListItemUiModel>>(emptyList())
    val listItems: StateFlow<List<PartyListItemUiModel>> = _listItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        // partyList 가 변하면 자동으로 uiModel 변환
        viewModelScope.launch {
            partyList.collect { list ->
                val uiList = list.map { PartyListItemUiModel.from(it) }
                _listItems.value = uiList
            }
        }
    }

    fun loadParties() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val parties = getPartyListUseCase()
                _partyList.value = parties
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUpcomingJoinedParties() {
        viewModelScope.launch {
            try {
                val upcoming = getUpcomingJoinedPartiesUseCase()
                _upcomingJoinedParties.value = upcoming
            } catch (e: Exception) {
                // 상단만 실패하면 조용히 처리
            }
        }
    }

    fun refresh() {
        loadParties()
        loadUpcomingJoinedParties()
    }

    fun handleAction(summary: PartySummary) {
        viewModelScope.launch {
            try {
                if (summary.isJoined) {
                    PartyServiceLocator.leavePartyUseCase(summary.partyId)
                } else {
                    PartyServiceLocator.joinPartyUseCase(summary.partyId)
                }
                refresh()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}
