package com.erwanvallerie.todo.user

import android.net.Uri
import com.erwanvallerie.todo.network.Api
import com.erwanvallerie.todo.network.UserInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class UserInfoRepository {
    private val webService = Api.userWebService

    private fun convert(bytes: ByteArray): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = bytes.toRequestBody()
        )
    }
    suspend fun getInfo() : UserInfo? {
        val response = webService.getInfo();
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun handleImage(bytes:ByteArray){
        webService.updateAvatar(convert(bytes))
    }

    suspend fun updateData(u: UserInfo) {
        webService.update(u);
    }
}