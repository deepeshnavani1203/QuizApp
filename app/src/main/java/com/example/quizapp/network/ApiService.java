package com.example.quizapp.network;

import com.example.quizapp.models.Quiz;
import com.example.quizapp.models.ResultResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/quizzes")
    Call<List<Quiz>> getQuizzes();

    @GET("api/quizzes/{id}")
    Call<Quiz> getQuizById(@Path("id") String id);

    @POST("api/results/submit")
    Call<ResultResponse> submitResult(@Body Object resultData);
}
