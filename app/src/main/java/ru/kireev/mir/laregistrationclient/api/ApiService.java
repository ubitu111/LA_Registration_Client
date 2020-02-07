package ru.kireev.mir.laregistrationclient.api;


import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import ru.kireev.mir.laregistrationclient.pojo.Response;

public interface ApiService {
    @GET("searchers")
    Observable<List<Response>> getResponse();

    @POST("searchers")
    Call<Response> postResponse(@Body Response response);
}
