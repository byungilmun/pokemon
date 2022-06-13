package com.example.pokemon.domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SpriteInfo {

    @SerializedName("back_female")
    private String backFemale;

    @SerializedName("back_shiny_female")
    private String backShinyFemale;

    @SerializedName("back_default")
    private String backDefault;

    @SerializedName("front_female")
    private String frontFemale;

    @SerializedName("front_shiny_female")
    private String frontShinyFemale;

    @SerializedName("back_shiny")
    private String backShiny;

    @SerializedName("front_default")
    private String frontDefault;

    @SerializedName("front_shiny")
    private String frontShiny;

    public String getFrontDefault() {
        return frontDefault;
    }

    public String getFrontShiny() {
        return frontShiny;
    }

    public String[] getAllSprites() {
        String[] sprites = {backFemale, backShinyFemale, backDefault, frontFemale, frontShinyFemale, backShiny,
                frontDefault, frontShiny};

        ArrayList<String> list = new ArrayList();
        for (String sprite : sprites) {
            if (sprite != null) {
                list.add(sprite);
            }
        }
        return list.toArray(new String[list.size()]);
    }
}
