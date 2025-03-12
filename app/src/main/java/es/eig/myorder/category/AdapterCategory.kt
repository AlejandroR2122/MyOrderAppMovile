package es.eig.myorder.category

import es.eig.myorder.Variables.Category
import es.eig.myorder.viewmodel.ProductViewModel

class AdapterCategory(private val viewModel: ProductViewModel) {

    fun onCategorySelected(category: Category) {
        println("Categoría seleccionada: ${category}")
        if(category.id == 0){
            viewModel.noFilter()
        }else{
            viewModel.filtrarCategoria(category) // Actualiza el LiveData con los productos filtrados
        }
    }
}