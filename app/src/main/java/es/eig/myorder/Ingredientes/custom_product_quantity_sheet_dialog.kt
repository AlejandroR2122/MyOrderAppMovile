package es.eig.myorder.Ingredientes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.eig.myorder.R
import es.eig.myorder.ServerConnection.ConnectionServer
import es.eig.myorder.Variables.Category
import es.eig.myorder.Variables.Ingrediente
import es.eig.myorder.Variables.Producto
import es.eig.myorder.Variables.ProductoShopping
import es.eig.myorder.ViewModelProviderSingleton
import es.eig.myorder.viewmodel.ProductViewModel

class CustomProductQuantitySheetDialog : BottomSheetDialogFragment() {
    private var producto: Producto? = null
    private lateinit var newProducto: Producto

    private val viewModel: ProductViewModel
        get() = ViewModelProviderSingleton.productViewModel // Usamos la instancia del singleton

    // Inicializar newProducto con un valor por defecto si producto es null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupProducto() // Inicializar newProducto lo antes posible
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProducto() // Asegúrate de que setupProducto se llama al abrir
        setupViewProducto(view) // Configura la vista con los valores iniciales
        listingIngredientsPredeterminados(view)
        listingIngredientsToAdd(view)
    }

    companion object {
        fun newInstance(producto: Producto?): CustomProductQuantitySheetDialog {
            val fragment = CustomProductQuantitySheetDialog()
            val args = Bundle()
            args.putSerializable("producto", producto)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(productoShopping: ProductoShopping?): CustomProductQuantitySheetDialog {
            val fragment = CustomProductQuantitySheetDialog()
            val args = Bundle()
            args.putSerializable("productoShopping", productoShopping)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_custom_product_quantity_sheet, container, false)
        setupProducto()
        setupViewProducto(view) // Configura la vista con los valores iniciales
        listingIngredientsPredeterminados(view)
        listingIngredientsToAdd(view)
        return view
    }

    private fun setupProducto() {
        val producto = arguments?.getSerializable("producto") as? Producto

        newProducto = if (producto != null) {
            producto.copyIndependent().apply {
                cantidad += 1
            }
        } else {
            // Configuración de un producto predeterminado
            Log.e(
                "CustomSheet",
                "Producto es nulo. Usando valor predeterminado."
            )
            Producto(
                id = -1,
                nombre = "Producto desconocido",
                precio = 0.0,
                cantidad = 1,
                descripcion = "Sin descripción",
                ingredientesExtras = mutableListOf(),
                ingredientesOriginales = mutableListOf(),
                categoria = mutableListOf(),
                imagen = "",
                isActived = true,
                isPersonalizable = true
            )
        }
    }


    fun setupViewProducto(view: View) {
        if (newProducto != null) {
            // setImg(view)        // Img Del producto
            view.findViewById<TextView>(R.id.txtProductName).text = newProducto.nombre
            view.findViewById<TextView>(R.id.txtProductPrice).text = newProducto.precio.toString()
            view.findViewById<TextView>(R.id.txtProductDescription).text = newProducto.descripcion
            val imgProduct = view.findViewById<ImageView>(R.id.imgProduct)
            setImg(imgProduct)

        } else {
            Log.e("CustomSheet", "Producto no está disponible para configurar la vista")
        }

        view.findViewById<ImageButton>(R.id.btnIncrease).setOnClickListener {
            val currentQuantity =
                view.findViewById<TextView>(R.id.txtQuantity).text.toString().toIntOrNull() ?: 0
            view.findViewById<TextView>(R.id.txtQuantity).text = (currentQuantity + 1).toString()
            addProducto()
        }

        view.findViewById<ImageButton>(R.id.btnDecrease).setOnClickListener {
            if (newProducto.cantidad > 1) {
                val currentQuantity =
                    view.findViewById<TextView>(R.id.txtQuantity).text.toString().toIntOrNull() ?: 0
                view.findViewById<TextView>(R.id.txtQuantity).text =
                    (currentQuantity - 1).toString()
                restarProducto()
            }
        }

        view.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            AddNewProductoModificated()
            dismiss()
        }
    }
    fun setImg( imgProducto: ImageView){
        val connection = ConnectionServer()
        newProducto.let {
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
    }

    fun listingIngredientsPredeterminados(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerProductIngredients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = IngredientesPredeterminadosAdapter(newProducto,viewModel)
    }


    fun listingIngredientsToAdd(view: View) {
        // Obtengo el recycledView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerIngredients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Creo un IngredienteAdapter para manejarlo

        recyclerView.adapter = IngredienteAddAdapter(newProducto, viewModel)
    }

    fun addProducto() {
        newProducto.cantidad += 1
    }

    fun restarProducto() {
        if (newProducto.cantidad > 1) {
            newProducto.cantidad -= 1
        } else {

        }
    }

    fun AddNewProductoModificated() {
        viewModel.addProductShoppingList(newProducto)
    }

}
