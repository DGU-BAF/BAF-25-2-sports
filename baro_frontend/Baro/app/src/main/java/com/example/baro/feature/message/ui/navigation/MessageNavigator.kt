package com.example.baro.feature.message.ui.navigation

import androidx.navigation.NavController
import com.example.baro.feature.message.ui.list.MessageListFragmentDirections

class MessageNavigator(
    private val navController: NavController
) {

    /**
     * 메시지 목록 화면 → 채팅방 화면 이동
     *
     * @param roomId 채팅방 ID
     * @param roomName 파티 이름
     */
    fun navigateToRoom(roomId: String, roomName: String) {
        val direction = MessageListFragmentDirections
            .actionMessageListFragmentToMessageRoomFragment(
                roomId = roomId,
                roomName = roomName
            )
        navController.navigate(direction)
    }

    /**
     * 채팅방 → 이전 화면으로
     */
    fun navigateBack() {
        navController.navigateUp()
    }
}
