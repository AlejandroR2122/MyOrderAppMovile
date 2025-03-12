package es.eig.myorder.ServerConnection

import es.eig.myorder.Variables.Category
import es.eig.myorder.Variables.Ingrediente


data class ApiRespuesta(
    val success: Boolean,
    val message: String,

)
data class ApiModeloRespuesta(
    val success: Boolean,
    val message: String,
)
data class ProductoConnection(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val imagen: String,
    val cantidad: Int,
    val categorias: MutableList<Category>,
    val ingredientesOriginales: MutableList<Ingrediente> = mutableListOf(),
    val ingredientesExtras: MutableList<Ingrediente> = mutableListOf(),
    val personalizable: Boolean,
    val isActive: Boolean
)


data class ApiResponse(
    val message: String,
    val status: Int
)
