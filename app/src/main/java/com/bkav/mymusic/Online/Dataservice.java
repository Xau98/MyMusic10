package com.bkav.mymusic.Online;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Dataservice {

    @GET("SelectSongs.php")
    Call<List<SongOnline>> GetData();


}
