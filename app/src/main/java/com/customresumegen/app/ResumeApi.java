package com.customresumegen.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ResumeApi {
    @GET("resume")
    Call<Resume> getResume(@Query("name") String name);
}