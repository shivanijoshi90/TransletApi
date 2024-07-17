package com.example.transletapi

import com.google.gson.annotations.SerializedName

data class TransletApiModel(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Data(

	@field:SerializedName("translatedText")
	val translatedText: String? = null
)
