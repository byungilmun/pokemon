package com.example.pokemon.domain;

public class PokemonDetailedInfo {
    private String[] names;
    private int height;
    private int weight;
    private String sprite;
    private boolean isHabitatKnown;
    private Float latitude;
    private Float longitude;

    public PokemonDetailedInfo(String[] names, int height, int weight, String sprite, boolean isHabitatKnown,
                               Float latitude, Float longitude) {
        this.names = names;
        this.height = height;
        this.weight = weight;
        this.sprite = sprite;
        this.isHabitatKnown = isHabitatKnown;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String[] getNames() {
        return names;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public String getSprite() {
        return sprite;
    }

    public boolean isHabitatKnown() {
        return isHabitatKnown;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }
}
