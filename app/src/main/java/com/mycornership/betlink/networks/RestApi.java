package com.mycornership.betlink.networks;

import com.mycornership.betlink.models.BirthdayResponse;
import com.mycornership.betlink.models.BirthdaySticker;
import com.mycornership.betlink.models.Cashout;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.GameResponse;
import com.mycornership.betlink.models.Like;
import com.mycornership.betlink.models.MatchResponse;
import com.mycornership.betlink.models.News;
import com.mycornership.betlink.models.NewsResponse;
import com.mycornership.betlink.models.Notification;
import com.mycornership.betlink.models.Prediction;
import com.mycornership.betlink.models.Subscription;
import com.mycornership.betlink.models.TipsResponse;
import com.mycornership.betlink.models.User;
import com.mycornership.betlink.models.VideoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {

    @GET("betlink/v2/tips/{category}/{page}")
    Call<TipsResponse> fetchTips(@Path("category") String cat, @Path("page") int page);

    @GET("betlink/v2/tips/related/{user}/{category}/{id}")
    Call<TipsResponse> fetchTipsByUser(@Path("category") String cat, @Path("user") String user, @Path("id") long id);

    @GET("betlink/v2/videos/{page}")
    Call<VideoResponse> fetchVideos(@Path("page") int page);

    @GET("betlink/v2/videos/related/{filter}/{id}")
    Call<VideoResponse> fetchRelatedVideos(@Path("filter")String category, @Path("id")String id);

    @GET("betlink/v2/fixtures/{date}/{page}")
    Call<MatchResponse> fetchMatches(@Path("date") String date, @Path("page") int page);

    @GET("betlink/v2/birthdays/{date}")
    Call<BirthdayResponse> fetchBirthdays(@Path("date") String date);

    @GET("betlink/v2/highlights/{page}")
    Call<NewsResponse> fetchNews(@Path("page") int page);

    @GET("betlink/v2/games")
    Call<GameResponse> fetchGames();

    @GET("betlink/v2/news/related/{filter}/{id}")
    Call<NewsResponse> fetchRelatedNews(@Path("filter") String category, @Path("id") String id);

    @GET("betlink/v2/tournaments")
    Call<DataResponse> fetchTournaments();

    @GET("betlink/v2/match/lineup/{matchId}")
    Call<DataResponse> fetchLineup(@Path("matchId") long id);

    @GET("betlink/v2/user/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("betlink/v2/login/{username}")
    Call<DataResponse> dailyLogin(@Path("username") String username);

    @POST("betlink/v2/user")
    Call<DataResponse> addUser(@Body User user);

    @POST("betlink/v2/tip")
    Call<DataResponse> addTip(@Body Prediction tip);

    @POST("betlink/v2/tip/like")
    Call<DataResponse> likeTip(@Body Like like);

    @POST("betlink/v2/withdraw")
    Call<DataResponse> withdraw(@Body Cashout cashout);

    @GET("betlink/v2/bonus/news")
    Call<DataResponse> creditUser(@Query("username") String user, @Query("news_id") String id);

    @GET("betlink/v2/prediction")
    Call<DataResponse> updateStatus(@Query("status") String status, @Query("id") String id, @Query("username") String user, @Query("grade") String grade);

    @GET("betlink/v2/subscription/{username}")
    Call<DataResponse> userSub(@Path("username") String user);

    @GET("betlink/v2/subscription/validate/{reference}")
    Call<DataResponse> subValidate(@Path("reference") String ref);

    @POST("betlink/v2/sticker")
    Call<DataResponse> birthDaySticker(@Body BirthdaySticker sticker);

    @GET("betlink/v2/sticker")
    Call<List<BirthdaySticker>> celebStickers(@Query("celebrant") String id);

    @GET("betlink/v2/device")
    Call<DataResponse> sendToken(@Query("username")String username, @Query("token")String token);

    @GET("betlink/v2/notification")
    Call<List<Notification>> getNotification(@Query("username") String user);

    @GET("betlink/v2/news/{id}/{promoter}")
    Call<News> getNewsItem(@Path("id") String id, @Path("promoter") String prm, @Query("invitee") String invitee);

}
