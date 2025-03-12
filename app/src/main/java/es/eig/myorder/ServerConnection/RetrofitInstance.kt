package es.eig.myorder.ServerConnection
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance { // "http://192.168.0.89:3000"  "Emulador localHost "10.0.2.2:3000"
    const val BASE_URL = "http://10.0.2.2:3000"

    // Configuración para manejar JSON
    val apiJson: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convertidor para JSON
            .build()
            .create(ApiService::class.java)
    }

    // Configuración para manejar strings
    val apiString: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create()) // Convertidor para strings
            .build()
            .create(ApiService::class.java)
    }
}