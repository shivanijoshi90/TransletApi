package com.example.transletapi

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface TransletInterface {

    @FormUrlEncoded
    @POST("/translate")

    fun translateLanguage(
        @Header("x-rapidapi-host") host: String = "text-translator2.p.rapidapi.com",
        @Header("x-rapidapi-Key") key: String = "a3697cdde7msha2093f031f6c079p1111a9jsn82bc379cd12d",
        @Field("source_language ") source: String,
        @Field("target_language") target: String,
        @Field("text") text: String
    ): Call<TransletApiModel>
}
