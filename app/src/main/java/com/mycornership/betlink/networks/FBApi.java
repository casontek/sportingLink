package com.mycornership.betlink.networks;

import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.FBResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FBApi {

    @POST("102576254608581/photos")
    Call<FBResponse> postFBNoLink(@Query("url") String url, @Query("caption") String msg, @Query("link") String link, @Query("access_token") String key);

    @POST("102576254608581/photos")
    Call<ResponseBody> postFBPagePhoto(@Query("url") String url,@Query("caption") String msg, @Query("access_token") String key);

    @POST("102576254608581/photos")
    Call<ResponseBody> postFBNoLink2(@Query("url") String url, @Query("link") String link, @Query("access_token") String key);

    @POST("102576254608581/video")
    Call<ResponseBody> postFBVideo(@Query("url") String url, @Query("caption") String link, @Query("access_token") String key);
}
