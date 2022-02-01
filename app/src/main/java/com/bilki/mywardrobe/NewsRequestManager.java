package com.bilki.mywardrobe;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.bilki.mywardrobe.NewsModels.NewsApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NewsRequestManager {

    Context context;
    private static final String TAG = "bilki: NewsRequestMa: ";

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void getNewsHeadlines(OnFetchNewsDataListener listener, String query){

        CallNewsApi callNewsApi = retrofit.create(CallNewsApi.class);
        Call<NewsApiResponse> call = callNewsApi.callHeadlines(/*"sports",*/ query, context.getString(R.string.news_api_key));

        try {

            call.enqueue(new Callback<NewsApiResponse>() {
                @Override
                public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {

                    if (!response.isSuccessful()){

                        Log.d(TAG, "onResponse: error");

                    }

                    listener.onFetchData(response.body().getArticles(), response.message());

                }

                @Override
                public void onFailure(Call<NewsApiResponse> call, Throwable t) {

                    listener.onError("Request failed!");
                    Log.d(TAG, "onFailure: " + t.getMessage());

                }
            });

        }catch (Exception e){

            Log.d(TAG, "getNewsHeadlines: " + e.getMessage());

        }

    }

    public NewsRequestManager(Context context) {
        this.context = context;
    }

    public interface CallNewsApi {
        @GET("everything")
        Call<NewsApiResponse> callHeadlines(

//                @Query("category") String category,
                @Query("q") String query,
                @Query("apiKey") String api_key

        );

    }
}
