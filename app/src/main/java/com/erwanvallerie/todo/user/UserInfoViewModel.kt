package com.erwanvallerie.todo.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erwanvallerie.todo.network.UserInfo
import kotlinx.coroutines.launch

class UserInfoViewModel: ViewModel() {
    private val userRepository = UserInfoRepository();

    suspend fun getInfo(): UserInfo? {
        return userRepository.getInfo();
    }

    fun updateData(user: UserInfo) {
        viewModelScope.launch {
            userRepository.updateData(user);
        }
    }

    fun handleImage(bytes: ByteArray) {
        viewModelScope.launch {
            userRepository.handleImage(bytes);
        }
    }
}