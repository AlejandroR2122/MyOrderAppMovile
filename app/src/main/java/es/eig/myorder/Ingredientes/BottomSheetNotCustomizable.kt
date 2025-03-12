import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.eig.myorder.Carts.AdapterCarts
import es.eig.myorder.R
import es.eig.myorder.Variables.Producto
import es.eig.myorder.ViewModelProviderSingleton
import es.eig.myorder.viewmodel.ProductViewModel

class BottomSheetNotCustomizable : BottomSheetDialogFragment() {
    private val viewModel: ProductViewModel
        get() = ViewModelProviderSingleton.productViewModel // Usamos la instancia del singleton

    private var productoSelected: Producto? = null  // Inicializamos la variable como null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = true

                setupProduct()
                // Personalizamos los botones AÑADIR RESTAR CERRAR
                view?.findViewById<Button>(R.id.buttonAddIngredient)?.setOnClickListener {
                    addProduct()
                }
                view?.findViewById<Button>(R.id.buttonRemoveIngredient)?.setOnClickListener {
                    subtractProduct()
                }
                view?.findViewById<Button>(R.id.buttonCloseDialog)?.setOnClickListener {
                    addNewProducto()
                    dismiss()
                }
            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Recuperamos el Producto en onCreateView
        productoSelected = arguments?.getSerializable("producto") as? Producto
        return inflater.inflate(R.layout.bottom_sheet_ingredients, container, false)
    }

    private fun setupProduct() {
        val quantityProduct = view?.findViewById<TextView>(R.id.quantityProduct)

        if (quantityProduct != null) {
            productoSelected?.let {
                it.cantidad = 1
                // Asegúrate de convertir 'cantidad' a una cadena
                quantityProduct.text =
                    "Total: " + it.cantidad.toString() // Convertimos a String si 'cantidad' es un número
            }
        }
    }

    private fun subtractProduct() {
        productoSelected?.let { producto ->  // 'producto' es una referencia segura a 'productoSelected'
            val quantityProduct = view?.findViewById<TextView>(R.id.quantityProduct)
            if (producto.cantidad > 1) {
                producto.cantidad -= 1
                // Usamos 'producto' en lugar de 'productoSelected'
                quantityProduct?.text = "Total: " + producto.cantidad.toString()
            }
        }
    }

    fun addProduct() {
        productoSelected?.let { producto ->  // 'producto' es una referencia segura a 'productoSelected'
            val quantityProduct = view?.findViewById<TextView>(R.id.quantityProduct)
            producto.cantidad += 1  // Usamos 'producto' en lugar de 'productoSelected'
            quantityProduct?.text = "Total: " + producto.cantidad.toString()
        }
    }


    fun addNewProducto() {
        productoSelected?.let { producto ->
            viewModel.addProductShoppingList(producto)
        }
    }
}
