package com.obadajasem.blablabla.api;

import com.obadajasem.blablabla.model.Country;
import com.obadajasem.blablabla.model.Station;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface  RadioApi {

    @GET("stations/bycountry/syria")
    Call<List<Station>> getstations();


    @GET("stations/bycountry/{country}")
    Call<List<Station>> getStationsByCountry( @Path("country") String country);


    @GET("countries")
    Call<List<Country>> getCountriesList();


}
