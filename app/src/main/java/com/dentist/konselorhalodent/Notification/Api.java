package com.dentist.konselorhalodent.Notification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("send")
    Call<ResponseBody> sendNotification(
            @Field("token") String token,
            @Field("token1") String token1,
            @Field("title") String title,
            @Field("body") String body,
            @Field("image") String image
    );

    @FormUrlEncoded
    @POST("chat")
    Call<ResponseBody> sendNotificationChat(
            @Field("token") String token,
            @Field("title") String title,
            @Field("body") String body,
            @Field("image") String image
    );
}

