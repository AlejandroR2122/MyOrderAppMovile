package es.eig.myorder.viewmodel

import es.eig.myorder.Variables.Producto
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.eig.myorder.ServerConnection.ConnectionServer
import es.eig.myorder.ServerConnection.ProductoConnection
import es.eig.myorder.Variables.Category
import es.eig.myorder.Variables.Ingrediente
import es.eig.myorder.Variables.ProductoShopping

class ProductViewModel : ViewModel() {
    private var newIdList: Int = 0

    // MutableLiveData para la lista de productos
    private var _productsList =
        MutableLiveData<MutableList<Producto>>(mutableListOf())  // Variable local
    val productList: LiveData<MutableList<Producto>> get() = _productsList              // Variable pública para acceso externo

    // Productos filtrados
    private var _productosShoppingList = MutableLiveData<List<ProductoShopping>>()
    val productosShoppingList: MutableLiveData<List<ProductoShopping>> = _productosShoppingList

    // Lista ingredientes
    var ingredientsList: MutableList<Ingrediente> = mutableListOf() // Lista de Categorías

    var priceFinal: Double = 0.00

    // Lista categories
    private var _categoriasCargadas = MutableLiveData<Boolean>(false) // Estado de carga
    val categoriasCargadas: LiveData<Boolean> get() = _categoriasCargadas

    private var _categoryList = MutableLiveData<MutableList<Category>>(mutableListOf())
    val categoryList: LiveData<MutableList<Category>> get() = _categoryList

    var OrgProductosList: MutableList<Producto> = mutableListOf()

    lateinit var mesa:String

    init {


        val ingredientesList = mutableListOf(
            Ingrediente(id = 1, nombre = "Tomate", cantidad = 0, type = "Vegetal"),
            Ingrediente(id = 2, nombre = "Lechuga", cantidad = 0, type = "Vegetal"),
            Ingrediente(id = 3, nombre = "Queso Cheddar", cantidad = 0, type = "Lácteo"),
            Ingrediente(id = 4, nombre = "Carne de Res", cantidad = 0, type = "Proteína"),
            Ingrediente(id = 5, nombre = "Pepinillos", cantidad = 0, type = "Vegetal"),
            Ingrediente(id = 6, nombre = "Pan Brioche", cantidad = 0, type = "Carbohidrato"),
            Ingrediente(id = 7, nombre = "Salsa BBQ", cantidad = 0, type = "Condimento")
        )


        ingredientsList = ingredientesList

        // Añado todos los productos a la lista ShoppingList
        _productosShoppingList.value = emptyList()
        // Crear categorías


    }

    // Obtener categorías
    fun getCategories():List<Category> {
        return _categoryList.value ?: emptyList()
    }

    // Método para añadir un producto
    fun addProduct(producto: Producto) {
        newIdList += 1
        val productoShopping = ProductoShopping(
            id = producto.id,
            idList = newIdList,
            nombre = producto.nombre,
            precio = producto.precio,
            descripcion = producto.descripcion,
            imagen = producto.imagen,
            cantidad = producto.cantidad,
            categoria = producto.categoria,
            ingredientesOriginales = producto.ingredientesOriginales.toMutableList(),
            ingredientesExtras = producto.ingredientesExtras.toMutableList(),
            isPersonalizable = producto.isPersonalizable,
            isActived = producto.isActived
        )
        // Añadir el producto a la lista de compras
        val shoppingList = _productosShoppingList.value?.toMutableList() ?: mutableListOf()
        productoShopping.cantidad = 1
        shoppingList.add(productoShopping)

        _productosShoppingList.value = shoppingList // Notificar el cambio en la lista de compras

    }

    fun addProductShoppingList(producto: Producto) {
        newIdList += 1
        val productoShopping = ProductoShopping(
            id = producto.id,
            idList = newIdList,
            nombre = producto.nombre,
            precio = producto.precio,
            descripcion = producto.descripcion,
            imagen = producto.imagen,
            cantidad = producto.cantidad,
            categoria = producto.categoria,
            ingredientesOriginales = producto.ingredientesOriginales.toMutableList(),
            ingredientesExtras = producto.ingredientesExtras.toMutableList(),
            isPersonalizable = producto.isPersonalizable,
            isActived = producto.isActived
        )
        // Crear una copia del producto para que sea independiente del original
        val productoCopia = producto.copyIndependent() // Usar el método de copia independiente

        // Añadir el producto copiado a la lista de compras
        val shoppingList = _productosShoppingList.value?.toMutableList() ?: mutableListOf()
        shoppingList.add(productoShopping)
        _productosShoppingList.value = shoppingList // Notificar el cambio en la lista de compras
    }

    fun removeProduct(producto: ProductoShopping) {
        // Obtiene la lista mutable de productos de la lista de compras
        val shoppingList = _productosShoppingList.value?.toMutableList() ?: mutableListOf()

        // Eliminar el producto de la lista (usando remove)
        val removed = shoppingList.removeIf { it.idList == producto.idList }

        // Si se ha encontrado y eliminado el producto, notifica el cambio
        if (removed) {
            _productosShoppingList.value = shoppingList // Actualiza el LiveData
        }
    }

    // Método para restar un producto
    fun subtractProduct(producto: Producto) {
        // Restar el producto de la lista de compras (_productosShoppingList)
        val shoppingList = _productosShoppingList.value?.toMutableList() ?: mutableListOf()
        val existingShoppingProduct = shoppingList.find { it.nombre == producto.nombre }

        if (existingShoppingProduct?.cantidad != null && existingShoppingProduct.cantidad > 0) {
            existingShoppingProduct.cantidad -= 1 // Reducir la cantidad del producto en la lista de compras
            if (existingShoppingProduct.cantidad == 0) {
                shoppingList.remove(existingShoppingProduct) // Eliminar el producto si la cantidad es 0
            }
        }
        _productosShoppingList.value = shoppingList // Notificar el cambio en la lista de compras
    }


    // Método para cambiar la cantidad de un producto
    fun changeQuantity(producto: ProductoShopping, newQuantity: Int) {
        val product = _productsList.value?.find { it.nombre == producto.nombre }
        product?.cantidad = newQuantity
        _productsList.value = _productsList.value // Notificar el cambio

        // Cambiar la cantidad en la lista de compras
        val shoppingList = _productosShoppingList.value?.toMutableList() ?: mutableListOf()
        val existingShoppingProduct = shoppingList.find { it.nombre == producto.nombre }
        if (existingShoppingProduct != null) {
            existingShoppingProduct.cantidad = newQuantity
        }
        _productosShoppingList.value = shoppingList // Notificar el cambio
    }

    // Filtrar por categorías
    fun filtrarCategoria(categoria: Category) {
        val productosFiltrados: MutableList<Producto> = if (categoria.nombre.isNullOrEmpty()) {
            OrgProductosList // Si no hay categoría, mostrar todos los productos
        } else {
            OrgProductosList.filter { producto ->
                producto.categoria.any { it.id == categoria.id }
            }.toMutableList()
        }

        _productsList.value = productosFiltrados // Actualizar la lista filtrada
    }

    fun noFilter() {
        _productsList.value = OrgProductosList // Actualizar la lista filtrada
    }

    // Método para restar un ingrediente
    fun subtractIngredient(producto: Producto, ingredient: Ingrediente) {
        val selectProducto = _productsList.value?.find { it.nombre == producto.nombre }
        if (selectProducto != null) {
            // Filtrar la lista para eliminar el ingrediente con el nombre especificado
            selectProducto.ingredientesOriginales =
                selectProducto.ingredientesOriginales.filter { it.nombre != ingredient.nombre }
                    .toMutableList()
            // Notificar el cambio
            _productsList.value = _productsList.value
            println(selectProducto)
        }
    }

    // Método para añadir ingredientes
    fun addIngredients(producto: Producto, ingredient: Ingrediente) {
        val selectProducto = _productsList.value?.find { it.nombre == producto.nombre }
        if (selectProducto != null) {
            // Verificar e inicializar la lista de ingredientes si es null
            if (selectProducto.ingredientesOriginales == null) {
                selectProducto.ingredientesOriginales = mutableListOf()
            }
            // Añadir el ingrediente a la lista
            selectProducto.ingredientesOriginales.add(ingredient)
            // Notificar el cambio en la lista de productos
            _productsList.value = _productsList.value
            println(selectProducto)
        }
    }

    fun actualizarProducto(productoEditado: ProductoShopping) {
        val listaActual = productosShoppingList.value?.toMutableList() ?: mutableListOf()
        val index = listaActual.indexOfFirst { it.idList == productoEditado.idList }
        if (index != -1) {
            listaActual[index] = productoEditado
            productosShoppingList.value = listaActual // Notifica el cambio
        }
    }

    fun getCategoriasFetch() {
        val connections = ConnectionServer()
        connections.fetchCategorias { response ->
            if (response != null){
                val categoriasModificadas = mutableListOf<Category>(
                    Category(0, "Todas", "0", "") // Agregar una categoría "Todas"
                )
                println("CATEGORIAS " + response)
                categoriasModificadas.addAll(response)
                _categoryList.postValue(categoriasModificadas ?: mutableListOf()) // Evita valores nullos
                _categoriasCargadas.postValue(true) // Indica que la carga ha finalizado
            }

        }
    }

    fun getProductsFetch() {
        var listaDeProductos: MutableList<Producto> = mutableListOf()

        // CONEXION SERVIDOR
        val connections = ConnectionServer()
        connections.fetchProductos { response ->
            if (response != null) {
                println("Esto es una prueba " + response.toString())
                (response as? List<ProductoConnection>)?.forEach { producto ->

                    val ingredientesExtras: MutableList<Ingrediente> =
                        emptyList<Ingrediente>().toMutableList()

                    val productoAdd = Producto(
                        producto.id,
                        producto.nombre,
                        producto.precio,
                        producto.descripcion,
                        producto.imagen,
                        producto.cantidad,
                        producto.categorias,
                        producto.ingredientesOriginales,
                        ingredientesExtras,
                        producto.personalizable,
                        producto.isActive
                    )
                    listaDeProductos.add(productoAdd)
                    println("Producto agregado: ${productoAdd}")
                }
                listaDeProductos =
                    listaDeProductos.filter { it -> it.isActived != false }.toMutableList()
                // Lista original
                OrgProductosList = listaDeProductos

                _productsList.value =
                    listaDeProductos // Inicializa la lista completa de productos
            } else {
                println("Error al conectar" + response)
            }

        }
    }
}
