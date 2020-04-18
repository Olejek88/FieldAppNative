package ru.shtrm.fieldappnative.rest.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.shtrm.fieldappnative.serverapi.TokenSrv;

public interface ITokenService {
    @FormUrlEncoded
    @POST("/token")
    Call<TokenSrv> getByLabel(@Field("label") String tagId, @Field("grant_type") String garantType);

    @FormUrlEncoded
    @POST("/token")
    Call<TokenSrv> getByPassword(@Field("password") String tagId, @Field("grant_type") String garantType);
}
