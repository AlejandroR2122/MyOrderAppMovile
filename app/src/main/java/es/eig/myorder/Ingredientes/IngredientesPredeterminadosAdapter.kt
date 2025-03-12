package es.eig.myorder.Ingredientes

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.eig.myorder.R
import es.eig.myorder.Variables.Ingrediente
import es.eig.myorder.Variables.ProductBase
import es.eig.myorder.Variables.Producto
import es.eig.myorder.viewmodel.ProductViewModel

class IngredientesPredeterminadosAdapter(
    private val producto: ProductBase,
    private val viewModel: ProductViewModel? = null,
) : RecyclerView.Adapter<IngredientesPredeterminadosAdapter.IngredienteViewHolder>() {
    private var productoSelected:Producto?=null

    init {
        // Buscar y asignar el producto de la lista original
        productoSelected = viewModel?.OrgProductosList?.find { it.id == producto.id }
    }
    // Definir el ViewHolder para este adaptador
    inner class IngredienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.txtIngredienteNombre)
        val boxIngredienteView: CheckBox = itemView.findViewById(R.id.checkBoxIngrediente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingrediente_predeterminado, parent, false)
        return IngredienteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        // Obtener el producto seleccionado de la lista original
        val productoSeleccionado = viewModel?.OrgProductosList?.find { it.id == producto.id }
        val ingredientesOriginales = productoSeleccionado?.ingredientesOriginales ?: emptyList()

        // Asegurarse de que la posición sea válida
        if (position < ingredientesOriginales.size) {
            val ingrediente = ingredientesOriginales[position]
            holder.nombreTextView.text = ingrediente.nombre

            // Determinar el estado inicial del checkbox basado en los ingredientes actuales del producto
            val isChecked = producto.ingredientesOriginales.contains(ingrediente)

            // Aplicar el selector del checkbox personalizado dinámicamente
            holder.boxIngredienteView.setButtonDrawable(R.drawable.checkbox_ingredients)
            holder.boxIngredienteView.isChecked = isChecked

            // Definir los colores para el checkbox
            val colorChecked =
                ContextCompat.getColor(holder.itemView.context, R.color.checkbox_checked_color)
            val colorUnchecked =
                ContextCompat.getColor(holder.itemView.context, R.color.checkbox_unchecked_color)

            // Establecer el tinte inicial del botón
            holder.boxIngredienteView.buttonTintList =
                ColorStateList.valueOf(if (isChecked) colorChecked else colorUnchecked)

            // Configurar un listener para manejar los cambios de estado del checkbox
            holder.boxIngredienteView.setOnCheckedChangeListener { _, isNowChecked ->
                if (isNowChecked) {
                    addIngredients(ingrediente) // Añadir a los ingredientes actuales
                    holder.boxIngredienteView.buttonTintList = ColorStateList.valueOf(colorChecked)
                } else {
                    removeIngredients(ingrediente) // Eliminar de los ingredientes actuales
                    holder.boxIngredienteView.buttonTintList = ColorStateList.valueOf(colorUnchecked)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        // Manejar el tamaño en caso de que `ingredientes` sea nulo
        return productoSelected?.ingredientesOriginales?.size ?: 0
    }

    fun removeIngredients(item: Ingrediente) {
        // Asegurarse de que `producto.ingredientes` no sea nulo antes de filtrar
        val newIngredientes =
            producto.ingredientesOriginales.filter { it.nombre != item.nombre } ?: emptyList()
        println("aaaaaa" + newIngredientes)
        producto.ingredientesOriginales = newIngredientes.toMutableList()
        println("aaaaaa" + newIngredientes)

    }

    fun addIngredients(item: Ingrediente) {
        producto.ingredientesOriginales.add(item)
    }

}
