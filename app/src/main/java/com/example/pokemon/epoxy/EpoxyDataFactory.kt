package com.example.pokemon.epoxy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide.init
import com.example.pokemon.MainActivity
import com.example.pokemon.domain.PokemonNameApiResponse
import com.example.pokemon.domain.PokemonNameInfo
import com.example.pokemon.network.DemoApiInterface
import com.example.pokemon.network.PokemonApiManager
import com.example.pokemonkotlin.util.CustomComparator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

object EpoxyDataFactory {
    private var namesApiResponse: PokemonNameApiResponse? = null
    private val nameInfoList = mutableListOf<PokemonNameInfo>()
    private val nameList = ArrayList<String>()

    private val _syResult: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val syncResult: LiveData<Boolean>
        get() = _syResult

    init {
        fetchNameInfo()
    }

    private fun fetchNameInfo() {
        val pokemonApiManager = PokemonApiManager.getInstance()
        val demoApiService: DemoApiInterface = pokemonApiManager.pokemonDemoApiInterface
        val namesCallSync = demoApiService.getPokemonNames()
        namesCallSync.enqueue(object : Callback<PokemonNameApiResponse> {
            override fun onResponse(
                call: Call<PokemonNameApiResponse?>,
                response: Response<PokemonNameApiResponse?>
            ) {
                if (response == null) return
                namesApiResponse = response.body()
                if (namesApiResponse == null) return
                for (info in namesApiResponse!!.pokemons) {
                    if (info != null) {
                        nameInfoList.add(info)
                        for (name in info.names) {
                            Log.d("FUCKING", "name = " + name)
                            nameList.add(name)
                        }
                    }
                }
                Collections.sort(nameList, CustomComparator())
                _syResult.postValue(true)
            }

            override fun onFailure(
                call: Call<PokemonNameApiResponse?>,
                t: Throwable
            ) {
            }
        })
    }

    fun getItems(): List<String> {
        return nameList
    }
}