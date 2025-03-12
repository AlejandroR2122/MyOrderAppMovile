package es.eig.myorder.Carts

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import coil.load
import es.eig.myorder.Ingredientes.BottomSheetProductSelected
import es.eig.myorder.R
import es.eig.myorder.ServerConnection.ConnectionServer
import es.eig.myorder.viewmodel.ProductViewModel
import es.eig.myorder.Variables.Producto

class MainCarts(private val viewModel: ProductViewModel, val connections: ConnectionServer) {

    fun createCarts(context: Context, containerLayout: LinearLayout) {
        containerLayout.removeAllViews()

        val scrollView = ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val primaryColor = context.resources.getColor(R.color.primaryColor2, context.theme)
            containerLayout.setBackgroundColor(primaryColor)
        }

        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 120)
        }

        viewModel.productList.observe(context as LifecycleOwner) { productList ->
            val filteredProductList = productList.filter { it.isActived }
            linearLayout.removeAllViews()
            var rowLayout: LinearLayout? = null

            for (i in filteredProductList.indices) {
                if (i % 2 == 0) {
                    rowLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            weightSum = 2f
                        }
                    }
                }

                val item = filteredProductList[i]
                createProductCard(context, item, rowLayout)

                if (i % 2 != 0 || i == filteredProductList.size - 1) {
                    linearLayout.addView(rowLayout)
                }
            }
        }

        scrollView.addView(linearLayout)
        containerLayout.addView(scrollView)
    }

    private fun createProductCard(context: Context, item: Producto, rowLayout: LinearLayout?) {
        val nombre = item.nombre

        val productCardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                550 // Altura fija para que todos sean iguales
            ).apply {
                weight = 1f
                setMargins(16, 20, 16, 20)
            }
            radius = 20f
            cardElevation = 12f
            setCardBackgroundColor(Color.WHITE)
        }

        val productLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT // Ocupar todo el espacio disponible
            )
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            setOnClickListener {
                (context as? FragmentActivity)?.let {
                    val args = Bundle().apply {
                        putSerializable("producto", item)
                    }
                    BottomSheetProductSelected().apply {
                        arguments = args
                        show(it.supportFragmentManager, tag)
                    }
                }
            }
        }

        val productImgView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                300,
                300
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setMargins(0, 16, 0, 16) // Margen superior e inferior
            }
            scaleType = ImageView.ScaleType.CENTER_CROP

            connections.fetchDownloadUrl(item.imagen) { imageUrl ->
                imageUrl?.let {
                    load(it) {
                        crossfade(true)
                        placeholder(R.drawable.imagen_loanding)
                        error(R.drawable.imagen_loanding)
                    }
                } ?: Log.e("IMAGE_LOAD", "No se pudo obtener la URL de la imagen")
            }
        }

        val productNameTextView = TextView(context).apply {
            text = nombre
            textSize = 20f
            setTextColor(Color.parseColor("#4C241D"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        val detailsLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER
        }

        detailsLayout.addView(productNameTextView)
        productLayout.addView(productImgView)
        productLayout.addView(detailsLayout)
        productCardView.addView(productLayout)
        rowLayout?.addView(productCardView)
    }
}
