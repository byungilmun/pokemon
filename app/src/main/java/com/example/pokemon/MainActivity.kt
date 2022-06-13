package com.example.pokemon

import android.content.res.Configuration
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.LruCache
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.databinding.ActivityMainBinding
import com.example.pokemon.domain.*
import com.example.pokemon.epoxy.PokemonEpoxyController
import com.example.pokemon.network.DemoApiInterface
import com.example.pokemon.network.PokeApiInterface
import com.example.pokemon.network.PokemonApiManager
import com.example.pokemonkotlin.util.CustomComparator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val cache: LruCache<Any?, Any?> =
        LruCache<Any?, Any?>(CACHE_SIZE)
    private val nameInfoList = mutableListOf<PokemonNameInfo>()
    private val nameList = ArrayList<String>()

    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var adapter: PokemonListAdapter
    private lateinit var detailedInfoDialog: DetailedInfoDialog

    private var namesApiResponse: PokemonNameApiResponse? = null
    private var locationApiResponse: PokemonLocationApiResponse? = null

    private lateinit var filterLoadTask: FilterDataLoadTask
    private var firstItemPosition = 0

    private var initialLoad = false
    private var prevScrollState = false
    private var currentScrollState = false

    private val epoxyController: PokemonEpoxyController by lazy { PokemonEpoxyController(nameList) }

    enum class FetchType {
        DIALOG, CACHE
    }

    companion object {
        private const val INVALID_POKEMON_ID = -1
        private const val CACHE_SIZE = 30
        private const val UPDATE_DETAIL_INFO_MESSAGE = 100
        private const val UPDATE_INITIAL_LOADING_STATE = 101
        private const val UPDATE_FILTERED_LIST = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initData()
    }

    fun initUI() {
        initializeSearchLayout()
        loadingLayout = binding.loadingLayout
        recyclerView = binding.recyclerView
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentScrollState = false
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    currentScrollState = true
                }
                if (!currentScrollState && prevScrollState) {
                    mHandler.sendEmptyMessage(UPDATE_DETAIL_INFO_MESSAGE)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lm = recyclerView.layoutManager as LinearLayoutManager
                firstItemPosition = lm.findFirstVisibleItemPosition()
            }
        })
//        recyclerView.adapter = epoxyController.adapter
//        epoxyController.requestModelBuild()
        adapter = PokemonListAdapter(this, nameList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : PokemonListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, selectedName: String) {
                if (namesApiResponse == null) return
                val id = namesApiResponse!!.findIdByName(selectedName)
                if (id != INVALID_POKEMON_ID) {
                    showDetailsForId(id)
                } else {
                    showInvalidIdMessage()
                }
            }
        })

    }

    private fun showInvalidIdMessage() {
        Toast.makeText(
            this,
            resources.getString(R.string.invalid_id_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private val mDismissListener = View.OnClickListener {
        dissmissDialog()
    }

    private fun dissmissDialog() {
        val detailedInfoDialog = this.detailedInfoDialog
        if (detailedInfoDialog != null) {
            detailedInfoDialog.dismiss()
        }
    }

    private fun showDetailsForId(id: Int) {
        val detailedInfoDialog = DetailedInfoDialog(this, mDismissListener)
        detailedInfoDialog.show()
        val detailedInfo = getCacheInfo(id)
        if (detailedInfo != null) {
            detailedInfoDialog.updateDetailedInfo(detailedInfo)
        } else {
            fetchDetailForId(id, FetchType.DIALOG)
        }
        this.detailedInfoDialog = detailedInfoDialog
    }

    fun initData() {
        fetchNameInfo()
        fetchLocationInfo()
    }

    private fun fetchLocationInfo() {
        val pokemonApiManager = PokemonApiManager.getInstance()
        val demoApiService = pokemonApiManager.pokemonDemoApiInterface
        val locationCallSync = demoApiService.pokemonLocations
        locationCallSync.enqueue(object : Callback<PokemonLocationApiResponse> {
            override fun onFailure(call: Call<PokemonLocationApiResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<PokemonLocationApiResponse>,
                response: Response<PokemonLocationApiResponse>
            ) {
                locationApiResponse = response.body()!!
            }
        })
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
                adapter.notifyDataSetChanged()
//                epoxyController.requestModelBuild()
                mHandler.sendEmptyMessage(UPDATE_DETAIL_INFO_MESSAGE)
                mHandler.sendEmptyMessage(UPDATE_INITIAL_LOADING_STATE)
            }

            override fun onFailure(
                call: Call<PokemonNameApiResponse?>,
                t: Throwable
            ) {
            }
        })
    }

    fun initializeSearchLayout() {
        editText = binding.searchText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (recyclerView != null) {
                    recyclerView.stopScroll()
                }
                val msg = mHandler.obtainMessage(UPDATE_FILTERED_LIST, s)
                mHandler.sendMessageDelayed(msg, 100)
            }

        })
    }

    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UPDATE_DETAIL_INFO_MESSAGE -> {
                    val queryList: List<String> = makeQueryList(adapter.getFilterDataList())
                    if (!this@MainActivity::filterLoadTask.isInitialized) return
                    if (filterLoadTask != null && filterLoadTask!!.getStatus() == AsyncTask.Status.RUNNING) {
                        filterLoadTask!!.cancel(true)
                    }
                    val tempLoadTask = FilterDataLoadTask()
                    tempLoadTask.execute(queryList)
                    filterLoadTask = tempLoadTask
                }
                UPDATE_INITIAL_LOADING_STATE -> if (!initialLoad) {
                    if (loadingLayout!!.visibility == View.VISIBLE) {
                        loadingLayout.visibility = View.GONE
                    }
                    initialLoad = true
                } else {
                    if (loadingLayout!!.visibility == View.VISIBLE) {
                        loadingLayout.visibility = View.GONE
                    }
                }
                UPDATE_FILTERED_LIST -> {
                    val editable = msg.obj as Editable
                    if (editable.toString().length == 0) {
                        adapter.getFilter().filter("")
                    } else {
                        adapter.getFilter().filter(editable)
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun makeQueryList(filterList: List<String>): List<String> {
        val queryList = ArrayList<String>()
        var cache_start_offset = 5;
        var cache_end_offset = 25
        var firstPosition = firstItemPosition
        val size = filterList.size
        var startPosition =
            if (firstPosition - cache_start_offset <= 0) 0 else firstPosition - cache_start_offset
        var endPosition =
            if (firstPosition + cache_end_offset > size - 1) size - 1 else firstPosition + cache_end_offset

        for (i in startPosition..endPosition) {
            var name = filterList.get(i)
            var id = namesApiResponse?.findIdByName(name)
            if (id?.let { getCacheInfo(it) } == null) {
                queryList.add(name)
            }
        }
        return queryList
    }

    inner class FilterDataLoadTask : AsyncTask<List<String>, Void, PokemonDetailedInfo>() {
        override fun doInBackground(vararg lists: List<String>?): PokemonDetailedInfo? {
            val list = lists[0]
            if (list != null) {
                for (name in list) {
                    var id = namesApiResponse?.findIdByName(name)
                    if (id == INVALID_POKEMON_ID) {
                        continue
                    }
                    id?.let { fetchDetailForId(it, FetchType.CACHE) }
                }
            }
            return null
        }
    }

    private fun fetchDetailForId(id: Int, fetchType: FetchType) {
        val pokemonApiManager = PokemonApiManager.getInstance()
        val pokeApiService: PokeApiInterface = pokemonApiManager.pokeApiInterface
        val pokeCallSync: Call<PokeApiResponse> = pokeApiService.getPokemonInfo(id)

        pokeCallSync?.enqueue(object : Callback<PokeApiResponse?> {
            override fun onResponse(
                call: Call<PokeApiResponse?>,
                response: Response<PokeApiResponse?>
            ) {
                val pokeApiResponse = response.body()
                val location = locationApiResponse?.findLocationById(id)
                val pokemonDetails: PokemonDetailedInfo
                pokemonDetails = if (location == null) {
                    PokemonDetailedInfo(
                        namesApiResponse!!.findNamesById(id),
                        pokeApiResponse!!.height,
                        pokeApiResponse.weight,
                        pokeApiResponse.representativeSprite,
                        false,
                        null,
                        null
                    )
                } else {
                    PokemonDetailedInfo(
                        namesApiResponse!!.findNamesById(id),
                        pokeApiResponse!!.height,
                        pokeApiResponse.weight,
                        pokeApiResponse.representativeSprite,
                        true,
                        location.latitude,
                        location.longitude
                    )
                }
                if (FetchType.DIALOG == fetchType) {
                    if (detailedInfoDialog != null) {
                        detailedInfoDialog!!.updateDetailedInfo(pokemonDetails)
                    }
                }
                addInfoToCache(id, pokemonDetails)
            }

            override fun onFailure(
                call: Call<PokeApiResponse?>,
                t: Throwable
            ) {
            }
        })
    }

    fun addInfoToCache(id: Int, pokemonDetails: PokemonDetailedInfo) {
        cache?.let {
            if (it.get(id) != null) {
                it.put(id, pokemonDetails)
            }
        }
    }

    fun getCacheInfo(id: Int): PokemonDetailedInfo? {
        if (cache != null) {
            if (cache.get(id) != null) {
                return cache.get(id) as PokemonDetailedInfo?
            }
        }
        return null
    }

    private fun showInvalidMessage() {
        Toast.makeText(this, "해당하는 포켓몬이 없습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dissmissDialog()
        if (filterLoadTask != null) {
            if (filterLoadTask!!.status == AsyncTask.Status.RUNNING) {
                filterLoadTask!!.cancel(true)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}