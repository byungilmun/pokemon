package com.example.pokemon.domain;

import com.google.gson.annotations.SerializedName;

public class PokeApiResponse {

    @SerializedName("height")
    private int height;

    @SerializedName("weight")
    private int weight;

    @SerializedName("sprites")
    private SpriteInfo sprites;

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public SpriteInfo getSprites() {
        return sprites;
    }

    public String getRepresentativeSprite() {
        if (sprites == null) {
            return null;
        }

        String[] allSprites = sprites.getAllSprites();

        if (allSprites.length == 0) {
            return null;
        }

        if (sprites.getFrontDefault() == null) {
            for (String sprite : allSprites) {
                if (sprite != null && !sprite.isEmpty()) {
                    return sprite;
                }
            }
        }

        return sprites.getFrontDefault();
    }

}
