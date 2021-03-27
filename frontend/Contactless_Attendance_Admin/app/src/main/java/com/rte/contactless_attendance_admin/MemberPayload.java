package com.rte.contactless_attendance_admin;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MemberPayload {
    @Multipart
    //testing fake api @POST("insertdata.php")
    @POST("insertdata")
    Call<ResponseBody> insertdata(
            @Part("name") RequestBody memberName,
            @Part("email") RequestBody memberEmail,
            @Part MultipartBody.Part file
    );
}
