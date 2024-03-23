package com.mygdx.satspacearcade;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyApi {
    @GET("/arcade.php")
    Call<List<RecordFromDB>> sendData(@Query("name") String name, @Query("score") int score);
}
