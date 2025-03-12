package es.eig.myorder.Ingredientes

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.eig.myorder.R
import es.eig.myorder.Variables.Ingrediente
import es.eig.myorder.Variables.ProductBase
import es.eig.myorder.viewmodel.ProductViewModel

class IngredienteAddAdapter(
    private val newProducto: ProductBase,
    private val viewModel: ProductViewModel
) :
    RecyclerView.Adapter<IngredienteAddAdapter.IngredienteViewHolder>() {

    private var ingredientsList: MutableList<Ingrediente> = viewModel.ingredientsList

    class IngredienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.txtIngredienteNombre)
        var cantidad: TextView = view.findViewById(R.id.txtIngredienteCantidad)
        val btnAdd: ImageButton = view.findViewById(R.id.btnIncrease)
        val btnRestar: ImageButton = view.findViewById(R.id.btnDecrease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingrediente_add, parent, false)
        return IngredienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        val ingrediente = ingredientsList[position]
        val ingredienteSelected = ingrediente.copy()

        val cantidadIngrediente = newProducto.ingredientesExtras.find { it.nombre == ingredienteSelected.nombre }

        setupViewIngredientes(holder) // Actualizar el valor de los ingredientes

        // Saco los ingredientes ya seleccionados y si no existe pone 0
        holder.nombre.text = ingredienteSelected.nombre
        if (cantidadIngrediente?.cantidad != 0 && cantidadIngrediente?.cantidad != null) {
            holder.btnRestar.visibility = View.VISIBLE

            val context = holder.itemView.context
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 32f // Bordes redondeados
                setColor(ContextCompat.getColor(context, R.color.colorSelected))
            }
            holder.cantidad.background = drawable
        } else {
            holder.btnRestar.visibility = View.INVISIBLE
            holder.cantidad.background = null // Restablece el fondo si no hay cantidad
        }

        holder.cantidad.text = cantidadIngrediente?.cantidad?.toString().takeIf { !it.isNullOrEmpty() } ?: "0"

        holder.cantidad.setPadding(30, 0, 30, 0) // Padding izquierdo y derecho más grande

        setupBtnIngrediente(holder,ingredienteSelected)

    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    // Función para añadir ingredientes
    fun addIngredient(item: Ingrediente): Ingrediente {
        var newIngrediente = newProducto.ingredientesExtras?.find { it.nombre == item.nombre }

        if (newIngrediente != null) {
            newIngrediente.cantidad += 1
        } else {
            item.cantidad = 1
            newProducto.ingredientesExtras?.add(item)
        }
        return newIngrediente ?: item
    }

    // Función para restar ingredientes
    fun restarIngredient(item: Ingrediente): Ingrediente {
        var restarIngrediente = newProducto.ingredientesExtras?.find { it.nombre == item.nombre }
        if (restarIngrediente != null) {
            if (restarIngrediente.cantidad > 1) {
                restarIngrediente.cantidad -= 1
            } else if (restarIngrediente.cantidad == 1) {
                restarIngrediente.cantidad = 0
                newProducto.ingredientesExtras?.removeIf { it.nombre == item.nombre }
            }
        }
        return restarIngrediente ?: item
    }

    fun setupBtnIngrediente(holder: IngredienteViewHolder ,ingredienteSelected:Ingrediente ) {

        holder.btnAdd.setOnClickListener {
            // Actualizamos el ingrediente en la lista de ingredientes extras
            val ingredienteAdd = addIngredient(ingredienteSelected)

            if (ingredienteAdd.cantidad != 0) {
                holder.btnRestar.apply {
                    visibility = View.VISIBLE
                }

                // Aplicar el fondo redondeado y color cuando se agregue un ingrediente
                val context = holder.itemView.context
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE

                    cornerRadius = 32f // Aumenta el radio para hacer los bordes más redondeados
                    setColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorSelected
                        )
                    ) // El color del fondo
                }
                holder.cantidad.background = drawable
            }
            // Actualizamos la cantidad en la vista
            holder.cantidad.text = ingredienteAdd.cantidad.toString()
        }

        // Manejar el clic en el botón de restar
        holder.btnRestar.setOnClickListener {
            val ingredienteRestado = restarIngredient(ingredienteSelected)
            if (ingredienteRestado.cantidad == 0) {
                // Si la cantidad es 0, restablecer el fondo a transparente
                holder.cantidad.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            android.R.color.transparent
                        )
                    )
                }
                holder.btnRestar.apply {
                    visibility = View.INVISIBLE
                }
            }
            holder.cantidad.text = ingredienteRestado.cantidad.toString()
            println(newProducto)
        }
    }

    fun setupViewIngredientes(holder: IngredienteViewHolder, ) {

    }
}
