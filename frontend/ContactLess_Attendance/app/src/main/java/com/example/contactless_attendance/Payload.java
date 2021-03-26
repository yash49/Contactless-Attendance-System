package com.example.contactless_attendance;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Payload {
    @Multipart
    @POST("verifyuser")
    Call<ResponseBody> upload(
            @Part("test_text") RequestBody test_text,
            @Part MultipartBody.Part file
    );
}
