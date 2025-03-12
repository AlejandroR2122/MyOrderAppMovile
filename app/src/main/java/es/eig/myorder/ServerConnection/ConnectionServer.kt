package es.eig.myorder.ServerConnection

import android.util.Log
import es.eig.myorder.Variables.Category
import es.eig.myorder.Variables.DownloadUrlResponse
import es.eig.myorder.Variables.OrderRequest
import es.eig.myorder.Variables.Producto
import es.eig.myorder.Variables.ProductoShopping
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectionServer {
    val ApiService = RetrofitInstance.apiJson
    fun fetchProductos(onResponse: (List<ProductoConnection>?) -> Unit) {
        ApiService.getProductsServer()
            .enqueue(object : retrofit2.Callback<List<ProductoConnection>> {
                override fun onResponse(
                    call: Call<List<ProductoConnection>>,
                    response: Response<List<ProductoConnection>>
                ) {
                    println("Esto es una prueba " + response.toString())

                    if (response.isSuccessful) {
                        println("✅ Conectado correctamente")

                        onResponse(response.body())
                    } else {
                        println("❌ Error de conexión")

                        // Imprimir el código de respuesta y el mensaje de error del servidor
                        println("⚠️ Código de respuesta: ${response.code()}")
                        println("⚠️ Mensaje de error: ${response.message()}")

                        // Intentar imprimir el cuerpo del error en JSON si existe
                        val errorBody = response.errorBody()?.string()
                        println("⚠️ Respuesta de error del servidor:\n$errorBody")

                        Log.e("RetrofitError", "Error del servidor: $errorBody")
                        onResponse(null)
                    }
                }


                override fun onFailure(call: Call<List<ProductoConnection>>, t: Throwable) {
                    println("❌ Error de conexión" + t.message)
                    println("Error de conexión"+ t)
                    println("Error de conexión"+ t.stackTrace)
                    println("Error de conexión"+ t.cause)
                    println("call " + call)
                    t.printStackTrace()  // Esto imprimirá el stack trace completo para más detalles

                    onResponse(null)
                }

            })

    }

    fun fetchCategorias(onResponse: (MutableList<Category>?) -> Unit) {
        ApiService.getCategoriesServer()
            .enqueue(object : retrofit2.Callback<MutableList<Category>> {
                override fun onResponse(
                    call: Call<MutableList<Category>>,
                    response: Response<MutableList<Category>>
                ) {
                    onResponse(response.body())
                }

                override fun onFailure(call: Call<MutableList<Category>>, t: Throwable) {
                    onResponse(null)
                }


            })
    }

    fun fetchDownloadUrl(key: String, onResponse: (String?) -> Unit) {
        // Primero revisamos si la URL ya está en caché
        UrlCache.getUrl(key)?.let { cachedUrl ->
            Log.d("CACHE_HIT", "Usando URL en caché para la clave: $key")
            onResponse(cachedUrl)
            return
        }

        // Si no está en caché, hacemos la petición
        ApiService.getDownloadUrl(key).enqueue(object : Callback<DownloadUrlResponse> {
            override fun onResponse(
                call: Call<DownloadUrlResponse>,
                response: Response<DownloadUrlResponse>
            ) {
                if (response.isSuccessful) {
                    val url = response.body()?.downloadUrl
                    if (url != null) {
                        UrlCache.saveUrl(key, url)  // Guardamos la URL en caché
                    }
                    onResponse(url)
                } else {
                    Log.e("API_ERROR", "Error en la respuesta: ${response.errorBody()?.string()}")
                    onResponse(null)
                }
            }

            override fun onFailure(call: Call<DownloadUrlResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error en la petición: ${t.message}")
                onResponse(null)
            }
        })
    }

    fun sendOrderFetch(orderRequest: OrderRequest, onResponse: (String?) -> Unit) {
        ApiService.sendOrderFetch(orderRequest).enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        onResponse(response.body())
                    } else {
                        onResponse(null)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    onResponse(null)
                }
            }
        )
    }

}