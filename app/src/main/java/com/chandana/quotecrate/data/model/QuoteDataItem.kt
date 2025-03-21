package com.chandana.quotecrate.data.model

import com.google.gson.annotations.SerializedName

data class QuoteDataItem(
    @SerializedName("author")
    val author: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("quote")
    val quote: String
)