package es.eig.myorder.SheetDialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.eig.myorder.Ingredientes.IngredienteAddAdapter
import es.eig.myorder.Ingredientes.IngredientesPredeterminadosAdapter
import es.eig.myorder.R
import es.eig.myorder.Variables.Producto
import es.eig.myorder.Variables.ProductoShopping
import es.eig.myorder.ViewModelProviderSingleton
import es.eig.myorder.viewmodel.ProductViewModel
import org.w3c.dom.Text

class EditProductoSheetDialog : BottomSheetDialogFragment() {
    private var producto: Producto? = null
    private lateinit var editedProducto: ProductoShopping

    private val viewModel: ProductViewModel
        get() = ViewModelProviderSingleton.productViewModel // Usamos la instancia del singleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupProducto() // Inicializar newProducto lo antes posible
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupProducto() // Asegúrate de que setupProducto se llama al abrir
        setupViewProducto(view) // Configura la vista con los valores iniciales
        listingIngredientsPredeterminados(view)
        listingIngredientsToAdd(view)
    }

    companion object {
        fun newInstance(producto: Producto?): EditProductoSheetDialog {
            val fragment = EditProductoSheetDialog()
            val args = Bundle()
            args.putSerializable("producto", producto)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(productoShopping: ProductoShopping?): EditProductoSheetDialog {
            val fragment = EditProductoSheetDialog()
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
            inflater.inflate(R.layout.fragmen_edit_product_sheet_dialog, container, false)
        setupProducto()
        setupViewProducto(view) // Configura la vista con los valores iniciales
        listingIngredientsPredeterminados(view)
        listingIngredientsToAdd(view)
        println("PRODUCTO QUE BUSCO " + producto)
        return view
    }

    private fun setupProducto() {
        val producto = arguments?.getSerializable("productoShopping") as? ProductoShopping

        editedProducto = if (producto != null) {
            producto.copy()
        } else {
            ProductoShopping(
                id = -1,
                idList = null,
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
        if (editedProducto != null) {
            setupViewData(view)
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
            if (editedProducto.cantidad > 1) {
                val currentQuantity =view.findViewById<TextView>(R.id.txtQuantity).text.toString().toIntOrNull() ?: 0
                view.findViewById<TextView>(R.id.txtQuantity).text =(currentQuantity - 1).toString()
                restarProducto()
            }
        }

        view.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            // Llama al método del ViewModel para actualizar el producto
            viewModel.actualizarProducto(editedProducto)
            dismiss() // Cierra el diálogo
        }

    }

    fun listingIngredientsPredeterminados(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerProductIngredients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

         recyclerView.adapter = IngredientesPredeterminadosAdapter(editedProducto,viewModel)
    }


    fun listingIngredientsToAdd(view: View) {
        // Obtengo el recycledView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerIngredients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Creo un IngredienteAdapter para manejarlo

        recyclerView.adapter = IngredienteAddAdapter(editedProducto, viewModel)
    }


    fun setupViewData(view: View) {
        view.findViewById<TextView>(R.id.txtProductName).text = editedProducto.nombre
        view.findViewById<TextView>(R.id.txtProductPrice).text = editedProducto.precio.toString()
        view.findViewById<TextView>(R.id.txtProductDescription).text = editedProducto.descripcion


    }
    fun addProducto() {
        editedProducto.cantidad += 1
    }

    fun restarProducto() {
        if (editedProducto.cantidad > 1) {
            editedProducto.cantidad -= 1
        } else {

        }
    }


}
