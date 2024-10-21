package com.example.lab10_1.data


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductoApiService {
    // Obtener la lista de productos
    @GET("api/productos/")
    suspend fun getProductos(): List<ProductoModel>

    // Obtener un producto específico por ID
    @GET("api/productos/{id}/")
    suspend fun getProducto(@Path("id") id: Int): Response<ProductoModel>

    // Crear un nuevo producto
    @POST("api/productos/")
    suspend fun createProducto(@Body producto: ProductoModel): Response<ProductoModel>

    // Actualizar un producto existente
    @PUT("api/productos/{id}/")
    suspend fun updateProducto(@Path("id") id: Int, @Body producto: ProductoModel): Response<ProductoModel>

    // Eliminar un producto por ID
    @DELETE("api/productos/{id}/")
    suspend fun deleteProducto(@Path("id") id: Int): Response<Unit>
}
