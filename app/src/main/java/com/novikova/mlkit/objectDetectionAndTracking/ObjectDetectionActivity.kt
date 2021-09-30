package com.novikova.mlkit.objectDetectionAndTracking

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.novikova.mlkit.R
import com.novikova.mlkit.REQUEST_CHOOSE_IMAGE
import com.novikova.mlkit.graphic.BitmapUtils
import com.novikova.mlkit.graphic.GraphicOverlay
import com.novikova.mlkit.graphic.VisionProcessorBase

class ObjectDetectionActivity: AppCompatActivity() {

    private val resLayout = R.layout.activity_object_detection

    private var imageUri: Uri? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var preview: ImageView? = null
    private var imageProcessor: VisionProcessorBase<List<DetectedObject>>? = null

    private var options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//        .enableClassification()
//        .enableMultipleObjects()
        .build()

    private var detectorState = ObjectDetectorState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resLayout)

        preview = findViewById(R.id.preview)
        graphicOverlay = findViewById(R.id.graphic_overlay)
        initSwitches()
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

    private fun createImageProcessor() {
        try {
            val objectDetectorOptions = options
            imageProcessor = ObjectDetectorProcessor(this, objectDetectorOptions)
        } catch (e: Exception) {
            toast("Can not create image processor: " + e.message)
        }
    }

    private fun createOptions(
        objectDetectorState: ObjectDetectorState
    ) {
        val streamMode = if (objectDetectorState.isStreamMode) {
            ObjectDetectorOptions.STREAM_MODE
        } else {
            ObjectDetectorOptions.SINGLE_IMAGE_MODE
        }

        val builder = ObjectDetectorOptions.Builder()
            .setDetectorMode(streamMode)

        if (objectDetectorState.isEnableClassification) builder.enableClassification()

        if (objectDetectorState.isEnableMultipleObjects) builder.enableMultipleObjects()

        options = builder.build()
        createImageProcessor()
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    private fun initSwitches() {
        findViewById<Switch>(R.id.switchStreamSingle).setOnCheckedChangeListener { _, isChecked ->
            detectorState = if (isChecked) {
                detectorState.copy(isStreamMode = false)
            } else {
                detectorState.copy(isStreamMode = true)
            }
            createOptions(detectorState)
            tryReloadAndDetectInImage()
        }

        findViewById<Switch>(R.id.switchClassification).setOnCheckedChangeListener { _, isChecked ->
            detectorState = if (isChecked) {
                detectorState.copy(isEnableClassification = true)
            } else {
                detectorState.copy(isEnableClassification = false)
            }
            createOptions(detectorState)
            tryReloadAndDetectInImage()
        }

        findViewById<Switch>(R.id.switchMultiple).setOnCheckedChangeListener { _, isChecked ->
            detectorState = if (isChecked) {
                detectorState.copy(isEnableMultipleObjects = true)
            } else {
                detectorState.copy(isEnableMultipleObjects = false)
            }
            createOptions(detectorState)
            tryReloadAndDetectInImage()
        }
    }

}