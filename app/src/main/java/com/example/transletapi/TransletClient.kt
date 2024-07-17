package com.example.transletapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TransletClient {


    companion object{

        val BASE_URL = "https://text-translator2.p.rapidapi.com/"

        var  retrofit: Retrofit? = null

        fun getTransletClient(): Retrofit?{

            retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
            return retrofit
        }
    }
}