package com.cryptospective.marco.cscpricewidget;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface RetrofitApi {
    @GET("/{server}")
    Call<ResponseBody> getData(@Path(value = "server", encoded = true) String server);
}
