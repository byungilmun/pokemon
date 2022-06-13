package com.example.pokemon.domain;

import com.google.gson.annotations.SerializedName;

public class PokemonNameApiResponse {

	@SerializedName("pokemons")
	private PokemonNameInfo[] pokemons;

	public String[] findNamesById(int id) {
		for (PokemonNameInfo each: pokemons) {
			if (each.getId() == id) {
				return each.getNames();
			}
		}
		return null;
	}

	public int findIdByName(String name) {
		int invalid_pokemon_id = -1;
		for (PokemonNameInfo each: pokemons) {
			for (String s : each.getNames()) {
				if (s.toLowerCase().equals(name.toLowerCase())) {
					return each.getId();
				}
			}
		}
		return invalid_pokemon_id;
	}

	public PokemonNameInfo[] getPokemons() {
		return pokemons;
	}
}
