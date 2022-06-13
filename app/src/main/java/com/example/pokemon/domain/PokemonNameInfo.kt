package com.example.pokemon.domain

import com.google.gson.annotations.SerializedName

class PokemonNameInfo {
    @SerializedName("id")
    var id = 0

    @SerializedName("names")
    lateinit var names: Array<String>
}