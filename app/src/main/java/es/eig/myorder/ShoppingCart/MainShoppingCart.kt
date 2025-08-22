package es.eig.myorder.ShoppingCart

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.eig.myorder.R
import es.eig.myorder.ServerConnection.ConnectionServer
import es.eig.myorder.Variables.OrderRequest
import es.eig.myorder.Variables.ProductoShopping
import es.eig.myorder.ViewModelProviderSingleton
import es.eig.myorder.viewmodel.ProductViewModel

class MainShoppingCart : AppCompatActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterShoppingList
    private lateinit var priceFinal: TextView

    private var nota: String = "" // Guardar la nota temporalmente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_shopping_cart)

        priceFinal = findViewById(R.id.priceFinal)
        viewModel = ViewModelProviderSingleton.productViewModel

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.productosShoppingList.observe(this) { products ->
            updatePrice(products)
            adapter = AdapterShoppingList(products) { product ->
                viewModel.removeProduct(product)
            }
            recyclerView.adapter = adapter
        }

        val btnConfirm: Button = findViewById(R.id.confirmOrderButton)
        btnConfirm.setOnClickListener { sendOrder() }

        val btnNota: TextView = findViewById(R.id.textNota)
        btnNota.setOnClickListener { showNoteDialog() }
    }

    private fun updatePrice(products: List<ProductoShopping>) {
        val totalPrice = products.sumOf { it.precio * it.cantidad }
        priceFinal.text = "Total: %.2f $".format(totalPrice)
        viewModel.priceFinal = totalPrice
    }

    private fun showNoteDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        builder.setTitle("📝 Añadir nota")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0) // margen interno
        }

        val input = EditText(this).apply {
            hint = "Escribe tu nota aquí..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.BLACK)
            background = ContextCompat.getDrawable(context, R.drawable.edit_text_background)
            setPadding(30, 20, 30, 20)
        }

        layout.addView(input)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            nota = input.text.toString()
            Toast.makeText(this, "Nota añadida: $nota", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancelar", null)

        builder.show()
    }


    private fun sendOrder() {
        val products = viewModel.productosShoppingList.value ?: return
        if (products.isEmpty()) {
            Toast.makeText(this, "No hay productos en el carrito", Toast.LENGTH_SHORT).show()
            return
        }

        val mesa = viewModel.mesa
        val total = products.sumOf { it.precio * it.cantidad }

        // Crear OrderRequest solo en este momento
        val orderRequest = OrderRequest(mesa, products, nota, total)

        val connection = ConnectionServer()
        connection.sendOrderFetch(orderRequest) { response ->
            if (response != null) {
                println("Response: $response")
            } else {
                println("Error sending order")
            }
        }
    }
}
