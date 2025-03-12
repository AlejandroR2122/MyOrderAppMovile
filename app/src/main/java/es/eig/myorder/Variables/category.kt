package es.eig.myorder.Variables

data class Category(
    val id: Int,                 // Identificador único para la categoría
    val nombre: String?,            // Nombre de la categoría
    val descripcion: String?,    // Descripción opcional de la categoría
    val imagen:String,
    val isActive: Boolean = true,  // Estado de la categoría (si está activa)

)