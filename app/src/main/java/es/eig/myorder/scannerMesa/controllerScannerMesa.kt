package es.eig.myorder.scannerMesa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import es.eig.myorder.MainActivity
import es.eig.myorder.R
import es.eig.myorder.ViewModelProviderSingleton
import es.eig.myorder.viewmodel.ProductViewModel

class controllerScannerMesa() : AppCompatActivity() {

    private val scannerLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            var mesa = result.contents
            if(mesa=="1"|| mesa=="2"|| mesa=="3"|| mesa=="4"|| mesa=="5"|| mesa=="6"|| mesa=="7"|| mesa=="8"|| mesa=="9"|| mesa=="10"){
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("mesa", result.contents)
                startActivity(intent)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startScanner() // Se ejecuta al abrir la actividad
    }
    private fun saveMesa( result: String) {

    }
    private fun startScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Escanea un código QR")
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)

        scannerLauncher.launch(options)
    }
}
