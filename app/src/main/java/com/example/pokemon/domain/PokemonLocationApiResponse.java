package com.example.pokemon.domain;

import com.google.gson.annotations.SerializedName;

public class PokemonLocationApiResponse {

	@SerializedName("pokemons")
	private PokemonLocationInfo[] locations;

	public PokemonLocationInfo findLocationById(int id) {
		for (PokemonLocationInfo each: locations) {
			if (each.getId() == id) {
				return each;
			}
		}
		return null;
	}
}
