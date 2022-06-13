package com.example.pokemon.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.*
import com.example.pokemon.R

@EpoxyModelClass(layout = R.layout.recyclerview_item)
abstract class PokemonEpoxyModel : EpoxyModelWithHolder<PokemonEpoxyModel.PokemonHolder>() {

    @EpoxyAttribute
    lateinit var name: String

    override fun bind(holder: PokemonHolder) {
        holder.textView.text = name
    }

    class PokemonHolder : EpoxyHolder() {
        lateinit var textView: TextView
        override fun bindView(itemView: View?) {
            textView = itemView?.findViewById(R.id.title)!!
        }

    }


}