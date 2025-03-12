package es.eig.myorder.ShoppingCart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import es.eig.myorder.R
import es.eig.myorder.ServerConnection.ConnectionServer
import es.eig.myorder.ServerConnection.UrlCache
import es.eig.myorder.SheetDialog.EditProductoSheetDialog
import es.eig.myorder.Variables.Producto
import es.eig.myorder.Variables.ProductoShopping
import es.eig.myorder.ViewModelProviderSingleton
import es.eig.myorder.viewmodel.ProductViewModel

class AdapterShoppingList(
    private val products: List<ProductoShopping>, // Esta es la lista original
    private val onRemoveClick: (ProductoShopping) -> Unit,

) : RecyclerView.Adapter<AdapterShoppingList.ProductViewHolder>() {

    private lateinit var viewModel: ProductViewModel

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val quantitySpinner: Spinner = itemView.findViewById(R.id.productQuantitySpinner)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val txtIngredientes: TextView = itemView.findViewById(R.id.extraIngredientsTextView)
        val btnRemove: Button = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_shoppinglist, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        viewModel = ViewModelProviderSingleton.productViewModel

        val product = products[position]  // Aquí utilizas la lista original
        holder.productName.text = product.nombre
        val precioCantidad = product.precio * product.cantidad
        holder.productPrice.text = "${product.precio} $"
        val connection = ConnectionServer()
        connection.fetchDownloadUrl(product.imagen) { imageUrl ->
            imageUrl?.let { url ->
                holder.productImage.load(url) {
                    crossfade(true)
                    placeholder(R.drawable.imagen_loanding) // Imagen mientras se carga
                    error(R.drawable.imagen_loanding) // Imagen si hay un error
                }
            } ?: run {
                Log.e("IMAGE_LOAD", "No se pudo obtener la URL de la imagen")
                holder.productImage.setImageResource(R.drawable.imagen_loanding) // Imagen en caso de fallo
            }
        }

        holder.removeButton.setOnClickListener {
            onRemoveClick(product)
        }
        if(product.isPersonalizable){
            holder.editButton.visibility = View.VISIBLE
        }else{
            holder.editButton.visibility = View.GONE
        }
        holder.editButton.setOnClickListener {
            // Obtén el FragmentActivity desde el contexto
            val activity = holder.itemView.context as? FragmentActivity
            activity?.let {
                // Crea una nueva instancia del BottomSheetFragment
                val nuevoFragment = EditProductoSheetDialog.newInstance(product)
                val args = Bundle()
                args.putSerializable("productoShopping", product) // Pasa el producto como argumento
                nuevoFragment.arguments = args

                // Muestra el BottomSheetDialogFragment usando supportFragmentManager
                nuevoFragment.show(it.supportFragmentManager, nuevoFragment.tag)
            }
        }


        val listIngredientes = product.ingredientesExtras + product.ingredientesOriginales

        val ingredientesText = "Este producto contiene: " + listIngredientes
            .map { it.nombre }  // Mapea la lista para obtener solo los nombres
            .joinToString(", ") // Une los nombres con una coma y espacio
        // Mostrar la cadena resultante en el TextView
        holder.txtIngredientes.text = ingredientesText

        // Crear la lista de cantidades de 1 a 20
        val quantities = (1..20).toList()
        val adapter =
            ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, quantities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.quantitySpinner.adapter = adapter

        // Establecer la selección del spinner, asegurándose de que la cantidad esté en el rango adecuado
        val initialQuantity = product.cantidad.coerceIn(
            1,
            20
        ) // Asegura que no se seleccione una cantidad fuera del rango
        holder.quantitySpinner.setSelection(quantities.indexOf(initialQuantity))

        // Listener para el Spinner
        holder.quantitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedQuantity = quantities[position]

                    if (product.cantidad != selectedQuantity) { // Verifica si realmente hay un cambio
                        product.cantidad = selectedQuantity
                        setValuesSpinner(product, selectedQuantity) // Llama solo si es necesario

                        val precioCantidad = product.precio * product.cantidad
                        holder.productPrice.text = "${precioCantidad} $"

                        // Actualiza el ViewModel
                        viewModel.changeQuantity(product, selectedQuantity)
                    }
                }

                fun setValuesSpinner(producto: ProductoShopping, cantidad: Int) {

                    viewModel.changeQuantity(producto, cantidad)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No hacer nada
                }
            }

        // Asegúrate de que el spinner pueda recibir clics
        holder.quantitySpinner.isFocusable = true
        holder.quantitySpinner.isFocusableInTouchMode = true
    }

    fun removeProduct(removeProducto: Producto) {
        //  viewModel.productosShoppingList.value?.find {it. =   }
    }

    override fun getItemCount(): Int = products.size  // Utilizas la lista original
}
