package es.eig.myorder.Carts

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import es.eig.myorder.Variables.Producto
import es.eig.myorder.viewmodel.ProductViewModel

/*
Esta clase se encarga de gestionar los productos del viewModel

-Suma cantidad de productos ViewModel
-Resta cantidad de productos ViewModel
 */
class AdapterCarts(context: Context, private val viewModel: ProductViewModel, val producto: Producto) {

    fun addProduct(productQuantityTextView: TextView) {
        viewModel.addProduct(producto)
        productQuantityTextView.text = "Total: ${producto.cantidad}"
    }

    fun subtractProduct(productQuantityTextView: TextView) {
        viewModel.subtractProduct(producto)
        productQuantityTextView.text = "Total: ${producto.cantidad}"

    }
}
