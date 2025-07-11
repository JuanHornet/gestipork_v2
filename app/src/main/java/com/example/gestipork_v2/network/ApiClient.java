package com.example.gestipork_v2.network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // 👈 muestra cuerpo del JSON

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // 🔍 Forzar Gson a usar solo campos con @Expose
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl("https://muwcidvjzvmjctqlvlhv.supabase.co/rest/v1/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }
    public static String getToken() {
        return SupabaseConfig.getAuthHeader(); // ya incluye el "Bearer"
    }

    public static String getApiKey() {
        return SupabaseConfig.getApiKey();
    }
}
