package com.cryptospective.marco.cscpricewidget;

import java.io.IOException;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;



public class Request {

    private RetrofitApi mRetrofitApi;
    private static Request request;

    public static Request getInstance() {
        if (request == null) {
            request = new Request();
        }
        return request;
    }


    public Request() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coinmarketcap.com/")
                .client(HttpHelper.getHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        mRetrofitApi = retrofit.create(RetrofitApi.class);
    }

    public Call getRequest(String function, final IRequestListener requestListener) {


        mRetrofitApi.getData(function).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    requestListener.onSuccess(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return null;
    }


    public interface IRequestListener {
        void onSuccess(String response);

        void onFail(String message);
    }
}

