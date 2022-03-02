package com.example.chatstation

import com.example.chatstation.Constants.Companion.CONTENT_TYPE
import com.example.chatstation.Constants.Companion.SERVER_KEY
import com.example.chatstation.model.PushNotification
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.ResponseBody
import retrofit2.Response


interface NotificationApi {

    @Headers("Authorization: key =$SERVER_KEY","CONTENT-type:$CONTENT_TYPE")
    @POST("fcm/send")

    suspend fun postNotification(
        @Body notification: PushNotification
    ):Response<ResponseBody>

}