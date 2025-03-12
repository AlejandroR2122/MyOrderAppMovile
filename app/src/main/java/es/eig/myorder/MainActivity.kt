package es.eig.myorder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import es.eig.myorder.Carts.MainCarts
import es.eig.myorder.ServerConnection.ConnectionServer
import es.eig.myorder.ServerConnection.UrlCache
import es.eig.myorder.ShoppingCart.MainShoppingCart
import es.eig.myorder.category.MainCategories
import es.eig.myorder.viewmodel.ProductViewModel

object ViewModelProviderSingleton {
    lateinit var productViewModel: ProductViewModel
}
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // CREAR VIEW MODEL
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        ViewModelProviderSingleton.productViewModel = viewModel // Asignar el ViewModel al singleton

        // Usar el singleton para obtener el ViewModel
        viewModel = ViewModelProviderSingleton.productViewModel

        // PETICION PARA OBTENER LOS PRODUCTOS/CATEGORIAS
        fetchserver()
        getMesa()
        //Crea las carts del main
        val connectionServer = ConnectionServer()
        val mainCarts = MainCarts(viewModel, connectionServer)
        val containerLayout = findViewById<LinearLayout>(R.id.cardContainer) // Cambiar a LinearLayout
        mainCarts.createCarts(this, containerLayout)


        // Observar cuándo las categorías están cargadas
        viewModel.categoriasCargadas.observe(this) { cargadas ->
            if (cargadas) {
                val categoriesList = MainCategories(viewModel)
                val categoryLayout = findViewById<LinearLayout>(R.id.navOptions)
                categoriesList.createCategories(this, categoryLayout)
            }
        }

        OpenerShoppingList()
    }

    private fun fetchserver() {
        viewModel.getProductsFetch()
        viewModel.getCategoriasFetch()
    }

    private fun getMesa(){
        val mesa = intent.getStringExtra("mesa")
        if (mesa != null) {
            viewModel.mesa = mesa
        }
    }
    private fun OpenerShoppingList() {
        val btn = findViewById<Button>(R.id.change)
        btn.setOnClickListener {
            // Crea un Intent para abrir la nueva actividad
            val intent = Intent(this, MainShoppingCart::class.java)
            startActivity(intent) // Inicia la nueva actividad
        }
    }

    private fun hideNavigationBar() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideNavigationBar()
        }
    }
/*
    // Método para filtrar productos
    fun filter(query: String) {
        filteredProducts = if (query.isEmpty()) {
            products // Muestra todos los productos si no hay consulta
        } else {
            products.filter { it.cantidad > 0 }
        }
        notifyDataSetChanged() // Notifica al adapter que los datos han cambiado
    }

 */
}
