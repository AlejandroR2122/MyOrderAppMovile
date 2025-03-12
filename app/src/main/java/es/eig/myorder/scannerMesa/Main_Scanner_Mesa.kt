package es.eig.myorder.scannerMesa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import es.eig.myorder.MainActivity
import es.eig.myorder.R
import es.eig.myorder.viewmodel.ProductViewModel


class Main_Scanner_Mesa : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scanner_mesa)

        val btn = findViewById<Button>(R.id.btn_start_order)
        btn.setOnClickListener { startOrder() }

        val btnPrueba= findViewById<Button>(R.id.btnPrueba)
        btnPrueba.setOnClickListener {
            val mesa ="2"
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("mesa", mesa)
            startActivity(intent)
        }
    }

    private fun startOrder() {
        val intent = Intent(this, controllerScannerMesa::class.java)
        startActivity(intent) // Inicia la actividad del escáner correctamente
    }
}
