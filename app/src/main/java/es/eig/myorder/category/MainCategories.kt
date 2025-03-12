package es.eig.myorder.category

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import coil.load
import es.eig.myorder.R
import es.eig.myorder.ServerConnection.ConnectionServer
import es.eig.myorder.Variables.Category
import es.eig.myorder.viewmodel.ProductViewModel

class MainCategories(private var viewModel: ProductViewModel) {
    private lateinit var AdapterCategory: AdapterCategory
    private var categoryList: List<Category> = viewModel.getCategories()

    fun createCategories(context: Context, containerLayout: LinearLayout) {
        containerLayout.removeAllViews()

        // Crear HorizontalScrollView
        val scrollViewHorizontal = HorizontalScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Crear LinearLayout para organizar las cartas horizontalmente
        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16) // Espaciado entre las cartas
        }

        // Crear las cartas para cada categoría
        for (item in categoryList) {
            createCategoriesCarts(context, item, linearLayout)
        }

        // Añadir el LinearLayout al HorizontalScrollView
        scrollViewHorizontal.addView(linearLayout)

        // Añadir el HorizontalScrollView al contenedor principal
        containerLayout.addView(scrollViewHorizontal)
    }

    private fun createCategoriesCarts(context: Context, item: Category, linearLayout: LinearLayout) {
        val nombre = item.nombre

        // Crear el CardView (cada tarjeta)
        val categoryCardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                180.dpToPx(context), // Ancho fijo de la tarjeta
                LinearLayout.LayoutParams.WRAP_CONTENT // Altura adaptable al contenido
            ).apply { setMargins(16, 8, 16, 8) } // Márgenes alrededor de cada carta
            radius = 12f
            cardElevation = 6f
        }

        // Crear la vista de imagen de la categoría
        val categoryImgView = ImageView(context).apply {
            val connection:ConnectionServer = ConnectionServer()
            println("ESTO ES IMGCATEGORY " +item.imagen)
            connection.fetchDownloadUrl(item.imagen) { imageUrl ->
                imageUrl?.let {
                    load(it) {
                        crossfade(true)
                        placeholder(R.drawable.imagen_loanding)
                        error(R.drawable.imagen_loanding)
                    }
                } ?: Log.e("IMAGE_LOAD", "No se pudo obtener la URL de la imagen")
            }
            setImageResource(R.drawable.toda_las_categorias_api) // Cambia esta imagen por la adecuada
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Ancho completo del CardView
                100.dpToPx(context) // Altura ajustada para la imagen
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        // Crear el TextView para el nombre de la categoría
        val nameTextView = TextView(context).apply {
            text = nombre
            textSize = 14f
            setTextColor(context.getColor(R.color.black))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 0) // Separación superior del texto
            }
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }

        // Crear un LinearLayout vertical para organizar los elementos dentro del CardView
        val cardContent = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 8)
            addView(categoryImgView)
            addView(nameTextView)
        }

        // Añadir el clic al CardView
        categoryCardView.setOnClickListener {
            AdapterCategory = AdapterCategory(viewModel)
            AdapterCategory.onCategorySelected(item)
        }

        categoryCardView.addView(cardContent)

        // Añadir la tarjeta al layout horizontal
        linearLayout.addView(categoryCardView)
    }
}

// Extensión para convertir dp a px
fun Int.dpToPx(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}
