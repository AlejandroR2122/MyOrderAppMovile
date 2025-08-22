import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.eig.myorder.Carts.AdapterCarts
import es.eig.myorder.R
import es.eig.myorder.ServerConnection.ConnectionServer
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


            }
        }

        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProduct()

        view.findViewById<Button>(R.id.buttonAddIngredient)?.setOnClickListener {
            addProduct()
        }

        view.findViewById<Button>(R.id.buttonRemoveIngredient)?.setOnClickListener {
            subtractProduct()
        }

        view.findViewById<Button>(R.id.buttonCloseDialog)?.setOnClickListener {
            addNewProducto()
            dismiss()
        }
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
        val quantityText = view?.findViewById<TextView>(R.id.quantityProduct)
        val imgProduct = view?.findViewById<ImageView>(R.id.imageViewProduct)
        val nameProduct = view?.findViewById<TextView>(R.id.textViewProductTitle)
        Log.e("ERROR " , imgProduct.toString())
        Log.e("ERROR " , productoSelected?.imagen.toString())
        if (productoSelected != null && quantityText != null && imgProduct != null) {
            productoSelected!!.cantidad = 1
            quantityText.text = "Total: ${productoSelected!!.cantidad}"
            nameProduct?.text = productoSelected!!.nombre
            setImg(imgProduct) // ya seguro
        } else {
            Log.e("BottomSheet", "Error: productoSelected o views son null")
        }
    }

    fun setImg(imgProducto: ImageView) {
        val connection = ConnectionServer()
        val producto = productoSelected

        if (producto == null) {
            Log.e("IMAGE_LOAD", "Producto es null")
            imgProducto.setImageResource(R.drawable.imagen_loanding)
            return
        }

        connection.fetchDownloadUrl(producto.imagen) { imageUrl ->
            if (imageUrl != null) {
                imgProducto.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.imagen_loanding)
                    error(R.drawable.imagen_loanding)
                }
            } else {
                Log.e("IMAGE_LOAD", "No se pudo obtener la URL de la imagen")
                imgProducto.setImageResource(R.drawable.imagen_loanding)
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
