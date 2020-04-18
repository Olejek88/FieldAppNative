package ru.shtrm.fieldappnative.rest.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.shtrm.fieldappnative.db.realm.User;

public interface IUserService {
    @GET("/account/me")
    Call<User> user();
}
