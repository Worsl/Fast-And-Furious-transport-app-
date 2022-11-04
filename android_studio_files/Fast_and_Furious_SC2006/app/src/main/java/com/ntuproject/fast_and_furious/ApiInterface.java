package com.ntuproject.fast_and_furious;

//import retrofit2.Call;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import io.reactivex.Observable;
import io.reactivex.Observable;

public interface ApiInterface {
    @GET("maps/api/directions/json")
    Single<Result> getDirection(@Query("mode") String mode,
                                @Query("transit_routing_preference") String preferance,
                                @Query("origin") String origin,
                                @Query("destination") String destination,
                                @Query("key") String key);
}