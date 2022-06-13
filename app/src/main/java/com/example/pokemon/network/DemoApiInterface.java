package com.example.pokemon.network;


import com.example.pokemon.domain.PokemonLocationApiResponse;
import com.example.pokemon.domain.PokemonNameApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DemoApiInterface {

	@GET("/pokemon_name")
	public Call<PokemonNameApiResponse> getPokemonNames();

	@GET("/pokemon_locations")
	public Call<PokemonLocationApiResponse> getPokemonLocations();

}
