package com.example.pokemon.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonApiManager {
	private static PokemonApiManager pokemonApiManager = null;

	private DemoApiInterface pokemonDemoApiInterface;
	private PokeApiInterface pokeApiInterface;

	private final String DEMO_API_BASE_URL = "https://62808a981020d852057e59f4.mockapi.io/demo/api/v1/";
	private final String POKE_API_BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

	public DemoApiInterface getPokemonDemoApiInterface() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
		okHttpBuilder.connectTimeout(10, TimeUnit.SECONDS);
		okHttpBuilder.readTimeout(10, TimeUnit.SECONDS);
		okHttpBuilder.writeTimeout(10, TimeUnit.SECONDS);
		okHttpBuilder.retryOnConnectionFailure(true);
		okHttpBuilder.addInterceptor(interceptor);
		OkHttpClient okHttpClient = okHttpBuilder.build();
		pokemonDemoApiInterface = new Retrofit.Builder()
				.baseUrl(DEMO_API_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.client(okHttpClient)
				.build()
				.create(DemoApiInterface.class);
		return pokemonDemoApiInterface;
	}

	public PokeApiInterface getPokeApiInterface() {
		OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
		okHttpBuilder.connectTimeout(10, TimeUnit.SECONDS);
		okHttpBuilder.readTimeout(10, TimeUnit.SECONDS);
		okHttpBuilder.writeTimeout(10, TimeUnit.SECONDS);
		okHttpBuilder.retryOnConnectionFailure(true);
		OkHttpClient okHttpClient = okHttpBuilder.build();

		pokeApiInterface = new Retrofit.Builder()
				.baseUrl(POKE_API_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.client(okHttpClient)
				.build()
				.create(PokeApiInterface.class);
		return pokeApiInterface;
	}

	public static PokemonApiManager getInstance()
	{
		if (pokemonApiManager == null)
			pokemonApiManager = new PokemonApiManager();

		return pokemonApiManager;
	}
}
