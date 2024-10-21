package com.example.lab10_1.data

import com.google.gson.annotations.SerializedName

data class ProductoModel(
    @SerializedName("id") val id: Int,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("precio") val precio: Double
)