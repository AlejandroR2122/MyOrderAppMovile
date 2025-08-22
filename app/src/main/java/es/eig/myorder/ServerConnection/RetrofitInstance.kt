package es.eig.myorder.ServerConnection
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance { // "http://192.168.0.89:3000"  "Emulador localHost "10.0.2.2:3000"  Ngrok =  " https://313b-2a02-9130-fd1b-daa5-b56e-5176-5052-3728.ngrok-free.app"
    const val BASE_URL = "https://9371-88-29-177-84.ngrok-free.app"

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