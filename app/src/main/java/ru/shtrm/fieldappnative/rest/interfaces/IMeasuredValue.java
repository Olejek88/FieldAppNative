package ru.shtrm.fieldappnative.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;

public interface IMeasuredValue {
    @GET("/measured-value")
    Call<List<MeasuredValue>> get();

    @GET("/measured-value")
    Call<List<MeasuredValue>> get(@Query("changedAfter") String changedAfter);

    @GET("/measured-value")
    Call<List<MeasuredValue>> getByUuid(@Query("uuid") String uuid);

    @GET("/measured-value")
    Call<List<MeasuredValue>> getByUuid(@Query("uuid[]") String[] uuid);

    @POST("/measured-value/upload-measured-value")
    Call<ResponseBody> send(@Body List<MeasuredValue> values);
}
