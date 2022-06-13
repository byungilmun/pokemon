package com.example.pokemon.epoxy

import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyController
import com.example.pokemon.domain.PokemonNameInfo

class PokemonEpoxyController(private val items: List<String>) : EpoxyController() {
    init {
        //items = EpoxyDataFactory.getItems()
    }

    override fun buildModels() {
        items.forEach { PokemonEpoxyModel_().name(it)  }
    }
}