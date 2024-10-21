package com.example.lab10_1.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.lab10_1.data.ProductoApiService
import com.example.lab10_1.data.ProductoModel
import kotlinx.coroutines.launch

@Composable
fun ProductoListScreen(navController: NavHostController, servicio: ProductoApiService) {
    val listaProductos = remember { mutableStateListOf<ProductoModel>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        refreshProductosList(servicio, listaProductos)
    }

    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ID", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.1f))
                Text("CÓDIGO", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.2f))
                Text("DESCRIPCIÓN", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.3f))
                Text("PRECIO", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.1f))
                Text("ACCIONES", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.1f))
            }
        }

        items(listaProductos) { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${item.id}", modifier = Modifier.weight(0.1f))
                Text(item.codigo, modifier = Modifier.weight(0.4f))
                Text(item.descripcion, modifier = Modifier.weight(0.3f)) // Mostrar descripción
                Text("$${item.precio}", modifier = Modifier.weight(0.1f)) // Mostrar precio
                IconButton(
                    onClick = { navController.navigate("productoVer/${item.id}") },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(
                    onClick = { navController.navigate("productoDel/${item.id}") },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}


suspend fun refreshProductosList(servicio: ProductoApiService, listaProductos: SnapshotStateList<ProductoModel>) {
    try {
        val response = servicio.getProductos()
        listaProductos.clear()
        listaProductos.addAll(response)
    } catch (e: Exception) {
        Log.e("API", "Error refreshing product list: ${e.message}")
    }
}

@Composable
fun ProductoSreem(navController: NavHostController, servicio: ProductoApiService, pid: Int? = null) {
    var id by remember { mutableStateOf(pid) }
    var codigo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pid) {
        if (pid != null) {
            try {
                val response = servicio.getProducto(pid)
                if (response.isSuccessful) {
                    response.body()?.let { producto ->
                        codigo = producto.codigo
                        descripcion = producto.descripcion
                        precio = producto.precio.toString()
                    }
                } else {
                    errorMessage = "Error al cargar el producto: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        TextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        TextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    errorMessage = null
                    try {
                        val producto = ProductoModel(id ?: 0, codigo, descripcion, precio.toDoubleOrNull() ?: 0.0)
                        val response = if (id == null) {
                            servicio.createProducto(producto)
                        } else {
                            servicio.updateProducto(id!!, producto)
                        }

                        if (response.isSuccessful) {
                            Log.d("API", "Producto ${if (id == null) "agregado" else "actualizado"} exitosamente")
                            navController.navigate("productos")
                        } else {
                            errorMessage = "Error: ${response.errorBody()?.string()}"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Procesando..." else "Guardar")
        }
    }
}

@Composable
fun ProductoEliminarScreen(navController: NavHostController, servicio: ProductoApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                navController.navigate("productos")
            },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar este producto?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                val response = servicio.deleteProducto(id)
                                if (response.isSuccessful) {
                                    Log.d("API", "Producto eliminado exitosamente")
                                    navController.navigate("productos")
                                } else {
                                    errorMessage = "Error al eliminar: ${response.errorBody()?.string()}"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error: ${e.message}"
                            } finally {
                                isLoading = false
                                showDialog = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Procesando..." else "Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("productos")
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}
