package es.eig.myorder.Ingredientes

import BottomSheetNotCustomizable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.eig.myorder.R
import es.eig.myorder.ServerConnection.ConnectionServer
import es.eig.myorder.Variables.Producto

class BottomSheetProductSelected : BottomSheetDialogFragment() {

    private var producto: Producto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recoger el producto de los argumentos
        producto = arguments?.getSerializable("producto") as? Producto
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del Bottom Sheet
        val view = inflater.inflate(R.layout.custom_bottom_product_selected, container, false)
        val imgProducto :ImageView = view.findViewById(R.id.productImage)
        // Referencias a los elementos de la UI
        //val productImage: ImageView = view.findViewById(R.id.productImage)
        val productName: TextView = view.findViewById(R.id.productName)
        val buttonNext: Button = view.findViewById(R.id.buttonNext)

        // Rellenar los datos del producto si `item` no es null
        producto?.let { product ->
            productName.text = product.nombre

        }
        val connection = ConnectionServer()
        producto?.let {
            connection.fetchDownloadUrl(it.imagen) { imageUrl ->
                imageUrl?.let { url ->
                    imgProducto.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.imagen_loanding) // Imagen mientras se carga
                        error(R.drawable.imagen_loanding) // Imagen si hay un error
                    }
                } ?: run {
                    Log.e("IMAGE_LOAD", "No se pudo obtener la URL de la imagen")
                    imgProducto.setImageResource(R.drawable.imagen_loanding) // Imagen en caso de fallo
                }
            }
        }

        // Configurar acción del botón
        buttonNext.setOnClickListener {
            val activity = context as? FragmentActivity
            activity?.let {
                // Creamos un Bundle donde pasaremos los datos
                val args = Bundle()
                args.putSerializable("producto", producto)

                if (producto?.isPersonalizable == false) {// Mostramos un DialogFragment si no es personalizable
                    val bottomSheetDialogFragment = BottomSheetNotCustomizable()
                    bottomSheetDialogFragment.arguments = args
                    bottomSheetDialogFragment.show(
                        it.supportFragmentManager,
                        bottomSheetDialogFragment.tag
                    )
                } else if (producto?.isPersonalizable == true) { // Mostramos un DialogFragment si es personalizable
                    // Creamos un Bundle para pasar los argumentos necesarios
                    val args = Bundle()
                    args.putSerializable("producto", producto) // `item` es el producto seleccionado

                    // Usamos el método `newInstance` para asegurarnos de que siempre se inicializa correctamente
                    val customBottomSheetDialogFragment =
                        CustomProductQuantitySheetDialog.newInstance(producto)

                    // Mostramos el fragmento
                    customBottomSheetDialogFragment.show(
                        it.supportFragmentManager,
                        "CustomBottomSheetDialogFragment"
                    )
                }
            }
            dismiss()
        }
        return view
    }

}
