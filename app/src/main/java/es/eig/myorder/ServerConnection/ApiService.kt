package es.eig.myorder.ServerConnection

import com.google.gson.annotations.SerializedName
import es.eig.myorder.Variables.Category
import es.eig.myorder.Variables.DownloadUrlResponse
import es.eig.myorder.Variables.OrderRequest
import es.eig.myorder.Variables.Producto
import es.eig.myorder.Variables.ProductoShopping
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    //Conexion Server
    @GET("/api/productos")
    fun getProductsServer(): Call<List<ProductoConnection>>

    //Conexion Api
    @GET("/server/productos") // El endpoint raíz
    fun connect(): Call<String> // Espera un String como respuesta

    @GET("/api/categorias")
    fun getCategoriesServer(): Call<MutableList<Category>>


    @GET("/s3/generateDownloadUrl/productos")
    fun getDownloadUrl(
        @Query("key") key: String
    ): Call<DownloadUrlResponse>

    @POST("/api/sendOrder")
    fun sendOrderFetch(
        @Body orderRequest: OrderRequest //
    ): Call<String>

}
