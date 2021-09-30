package com.novikova.mlkit.textdetector

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.novikova.mlkit.R
import com.novikova.mlkit.REQUEST_CHOOSE_IMAGE
import com.novikova.mlkit.graphic.BitmapUtils
import com.novikova.mlkit.graphic.GraphicOverlay
import com.novikova.mlkit.graphic.VisionProcessorBase

class TextDetectionActivity: AppCompatActivity() {

    private val resLayout = R.layout.activity_text_detection

    private var imageUri: Uri? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var preview: ImageView? = null
    private var imageProcessor: VisionProcessorBase<Text>? = null

    private var options = TextRecognizerOptions.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resLayout)

        preview = findViewById(R.id.preview)
        graphicOverlay = findViewById(R.id.graphic_overlay)
        findViewById<ImageView>(R.id.imageGallery).setOnClickListener {
            startChooseImageIntentForResult()
        }

        createImageProcessor()
        startChooseImageIntentForResult()
    }

    public override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.stop()
    }

    private fun startChooseImageIntentForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_picture)),
            REQUEST_CHOOSE_IMAGE
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) = if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
        imageUri = data!!.data
        tryReloadAndDetectInImage()
    } else {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createImageProcessor() {
        try {
            imageProcessor = TextRecognitionProcessor(this, options)
        } catch (e: Exception) {
            toast("Can not create image processor: " + e.message)
        }
    }

    private fun tryReloadAndDetectInImage() {
        if (imageUri == null) return
        val imageBitmap = BitmapUtils.getBitmapFromContentUri(contentResolver, imageUri) ?: return
        graphicOverlay!!.clear()
        preview!!.setImageBitmap(imageBitmap)
        if (imageProcessor != null) {
            graphicOverlay!!.setImageSourceInfo(
                imageBitmap.width, imageBitmap.height, false
            )
            detectImage(imageBitmap)

        } else {
            toast("Null imageProcessor, please check adb logs for imageProcessor creation error")
        }
    }

    private fun detectImage(bitmap: Bitmap) {
        val process = imageProcessor!!.detectImage(bitmap, graphicOverlay)
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        process
            ?.addOnSuccessListener {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                graphicOverlay?.clear()
                imageProcessor!!.onSuccess(it, graphicOverlay!!)
            }
            ?.addOnFailureListener {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                toast(it.localizedMessage)
            }
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

}
