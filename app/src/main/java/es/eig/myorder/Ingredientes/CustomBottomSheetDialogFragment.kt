package es.eig.myorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.eig.myorder.Ingredientes.CustomProductQuantitySheetDialog
import es.eig.myorder.Variables.Producto

class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {

    // Variables para almacenar los datos
    private lateinit var producto: Producto
    private var productImageRes: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del BottomSheet
        return inflater.inflate(R.layout.custom_bottom_product_selected, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener los argumentos del fragmento
        arguments?.let { bundle ->
            producto = bundle.getSerializable("producto") as Producto // Recuperar el objeto
            productImageRes = bundle.getInt("productImage")  // Obtener la referencia de la imagen
        }

        // Configurar los elementos dinámicamente
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productNameTextView: TextView = view.findViewById(R.id.productName)
        val buttonNext: Button = view.findViewById(R.id.buttonNext)

        // Asignar datos dinámicamente: nombre e imagen del producto
        productNameTextView.text = producto.nombre  // Asignar el nombre del producto
        productImage?.let {
            productImage.setImageResource(R.drawable.hamburguesa)  // Asignar la imagen del producto
        }

        // Configura el botón para pasar a la siguiente acción
        buttonNext.setOnClickListener {
            // Crea una nueva instancia del BottomSheetFragment
            val nuevoFragment = CustomProductQuantitySheetDialog()
            val args = Bundle()
            args.putSerializable("producto", producto) // Asegúrate de que `producto` sea una instancia de Producto
            nuevoFragment.arguments = args
            // Realiza la transacción del fragmento
            nuevoFragment.show(parentFragmentManager, nuevoFragment.tag)

            // Cierra el BottomSheet actual
            dismiss()
        }
    }
}
