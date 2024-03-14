package pe.edu.idat.appgaleriafotos

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import pe.edu.idat.appgaleriafotos.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var archivo: File
    private var rutaFotoActual = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btntomarfoto.setOnClickListener(this)
        binding.btncompartirfoto.setOnClickListener(this)
    }
    override fun onClick(view: View) {
        when(view.id){
            R.id.btntomarfoto -> tomarFoto()
            R.id.btncompartirfoto -> compartirFoto()
        }
    }
    private fun compartirFoto() {
        /*Volley;OkHttp;Retrofit*/
        if(rutaFotoActual != ""){
            val contenidoUri = obtenerContenidoUri(File(rutaFotoActual))
            val intentImagen = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contenidoUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/jpeg"
            }
            val chooser = Intent.createChooser(intentImagen,
                "Compartir Foto")
            if(intentImagen.resolveActivity(packageManager) != null){
                startActivity(chooser)
            }
        }else{
            Toast.makeText(applicationContext,
                "Debe seleccionar una imagen para compartirlo",
                Toast.LENGTH_LONG).show()
        }
    }
    private fun tomarFoto() {
        //abrirCamara.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            it.resolveActivity(packageManager).also {
                componente ->
                crearImagenFoto()
                val fotoUri: Uri = FileProvider.getUriForFile(
                    applicationContext,
                    "pe.edu.idat.appgaleriafotos.fileprovider",
                    archivo)
                it.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri)

            }
        }
        abrirCamara.launch(intentCamara)
    }
    private val abrirCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        resultado ->
        if(resultado.resultCode == RESULT_OK){
            val fotoBitMap = convertirFotoBitMap()
            binding.ivfoto.setImageBitmap(fotoBitMap)
        }
    }

    private fun convertirFotoBitMap(): Bitmap{
        return BitmapFactory.decodeFile(archivo.toString())
    }
    private fun crearImagenFoto(){
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        archivo = File.createTempFile(
            "IMG_${System.currentTimeMillis()}",
            ".jpg",
            directorio)
        rutaFotoActual = archivo.absolutePath
    }
    fun obtenerContenidoUri(archivoFoto: File): Uri{
        return FileProvider.getUriForFile(applicationContext,
            "pe.edu.idat.appgaleriafotos.fileprovider", archivoFoto)
    }
}