package com.example.baro.feature.bot.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baro.feature.bot.data.BotRepository

class BotChatRoomViewModelFactory(
    private val repository: BotRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BotChatRoomViewModel::class.java)) {
            return BotChatRoomViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
