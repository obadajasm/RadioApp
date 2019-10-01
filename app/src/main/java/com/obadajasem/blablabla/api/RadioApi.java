package com.obadajasem.blablabla.api;

import com.obadajasem.blablabla.model.Station;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface  RadioApi {
    @GET("stations/bycountry/syria")
    Call<List<Station>> getstations();
}
