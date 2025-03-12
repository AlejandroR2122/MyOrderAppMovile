package es.eig.myorder.Ingredientes

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import es.eig.myorder.Carts.AdapterCarts
import es.eig.myorder.R
import es.eig.myorder.Variables.Producto
import es.eig.myorder.viewmodel.ProductViewModel

/*
Esta clase gestiona las funcionalidades de las ventanaSheetDialogFragment
que se genera al darle click sobre un pruducto,

- Sumar producto
- Restar prodcuto

 */
class AdapterSheetDialogFragment() {

     fun subtractProduct(item: Producto?,view: View?,context: Context,viewModel: ProductViewModel) {
        item?.let {  // Usamos el safe call operator para evitar que item sea null
            val quantityProduct = view?.findViewById<TextView>(R.id.quantityProduct)

            if (quantityProduct != null) {
                val adapterCart = AdapterCarts(context, viewModel, it)
                adapterCart.subtractProduct(quantityProduct)
            } else {
                Log.e("CustomBottomSheet", "TextView quantityProduct no encontrado")
            }
        } ?: run {
            Log.e("CustomBottomSheet", "Producto no encontrado")
        }
    }

    fun addProduct(item: Producto?,view: View?,context: Context,viewModel: ProductViewModel) {
        item?.let {  // Usamos el safe call operator para evitar que item sea null
            val quantityProduct = view?.findViewById<TextView>(R.id.quantityProduct)

            if (quantityProduct != null) {

                // Se le pasa context (no es necesario Ahora, ViewModel para acceder
                // a la funcion AddProducto/substractProduct , y el item:Producto

                val adapterCart = AdapterCarts(context, viewModel, it)
                adapterCart.addProduct(quantityProduct)
            } else {
                Log.e("CustomBottomSheet", "TextView quantityProduct no encontrado")
            }
        } ?: run {
            Log.e("CustomBottomSheet", "Producto no encontrado")
        }
    }


}