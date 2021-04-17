package com.dojo.passport.Notifications;

import com.dojo.passport.Notifications.MyResponse;
import com.dojo.passport.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAt-G5WIY:APA91bEoG9ddKHNkWvC5492Iz49RJ1cabZwQ17PsAsV5Dgc7Rs2PlEC1Z1MDIJQP6hpP4TlKM6oQCWIKGC7rKAOiCvKSfvOHIv3Q_NqXu3iwIJLm1QpwVV4lOJEyJYtX9p4Ae8YY7jtW" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}