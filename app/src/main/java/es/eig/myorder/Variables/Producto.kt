package es.eig.myorder.Variables

import com.google.gson.annotations.SerializedName
import java.io.Serializable

interface ProductBase : Serializable {
    var id: Int
    var ingredientesExtras: MutableList<Ingrediente>
    var ingredientesOriginales: MutableList<Ingrediente>
    val nombre: String
    val precio: Double
    var cantidad: Int
}
data class Producto(
    override var id: Int,
    override val nombre: String,
    override var precio: Double,
    var descripcion: String,
    val imagen: String,
    override var cantidad: Int,
    val categoria: MutableList<Category>,
    override var ingredientesOriginales: MutableList<Ingrediente> = mutableListOf(),
    override var ingredientesExtras: MutableList<Ingrediente> = mutableListOf(),
    val isPersonalizable: Boolean,
    val isActived: Boolean
) : ProductBase {
    fun copyIndependent(
        ingredientesExtras: MutableList<Ingrediente> = this.ingredientesExtras.toMutableList()
    ): Producto {
        return Producto(
            id,
            nombre,
            precio,
            descripcion,
            imagen,
            cantidad,
            categoria,
            ingredientesOriginales.toMutableList(),
            ingredientesExtras.toMutableList(),
            isPersonalizable,
            isActived
        )
    }
}



data class ProductoShopping(
    override var id: Int,
    val idList: Int?,
    override var nombre: String,
    override var precio: Double,
    var descripcion: String,
    val imagen: String,
    var categoria: MutableList<Category>,
    override var cantidad: Int,
    override var ingredientesOriginales: MutableList<Ingrediente> = mutableListOf(),
    override var ingredientesExtras: MutableList<Ingrediente> = mutableListOf(),
    val isPersonalizable: Boolean,
    val isActived: Boolean
) : ProductBase {
    fun copyIndependent(
        ingredientesExtras: MutableList<Ingrediente> = this.ingredientesExtras.toMutableList()
    ): Producto {
        return Producto(
            id,
            nombre,
            precio,
            descripcion,
            imagen,
            cantidad,
            categoria,
            ingredientesOriginales.toMutableList(),
            ingredientesExtras,
            isPersonalizable,
            isActived
        )
    }
}

data class OrderRequest(
    var numberBoard:String,
    var productosShoppingList: List<ProductoShopping>,
    var anotaciones: String,
    var total: Double
)

data class DownloadUrlResponse(
    @SerializedName("downloadUrl") val downloadUrl: String
)
