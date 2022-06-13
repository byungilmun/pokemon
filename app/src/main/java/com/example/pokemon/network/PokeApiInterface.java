package com.example.pokemon.network;

import com.example.pokemon.domain.PokeApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeApiInterface {

    @GET("/api/v2/pokemon/{id}")
    public Call<PokeApiResponse> getPokemonInfo(@Path("id") int id);

}
