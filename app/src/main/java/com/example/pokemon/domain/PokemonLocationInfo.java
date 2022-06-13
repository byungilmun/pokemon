package com.example.pokemon.domain;

import com.google.gson.annotations.SerializedName;

public class PokemonLocationInfo {

	@SerializedName("lat")
	private Float latitude;

	@SerializedName("lng")
	private Float longitude;

	@SerializedName("id")
	private int id;

	public Float getLatitude() {
		return latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public int getId() {
		return id;
	}
}
