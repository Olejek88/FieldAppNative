package ru.shtrm.fieldappnative.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.shtrm.fieldappnative.db.realm.Channel;

public interface IChannel {
    @GET("/channel")
    Call<List<Channel>> get();

    @GET("/channel")
    Call<List<Channel>> get(@Query("changedAfter") String changedAfter);

    @GET("/channel")
    Call<List<Channel>> getById(@Query("id") String id);

    @GET("/channel")
    Call<List<Channel>> getById(@Query("id[]") String[] id);

    @GET("/channel")
    Call<List<Channel>> getByUuid(@Query("uuid") String uuid);

    @GET("/channel")
    Call<List<Channel>> getByUuid(@Query("uuid[]") String[] uuid);
}
