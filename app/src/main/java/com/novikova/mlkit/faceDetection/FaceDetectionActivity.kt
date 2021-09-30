package com.novikova.mlkit.faceDetection

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
import com.google.mlkit.vision.face.Face
import com.novikova.mlkit.facedetector.FaceDetectorProcessor
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.novikova.mlkit.*
import com.novikova.mlkit.graphic.BitmapUtils
import com.novikova.mlkit.graphic.GraphicOverlay
import com.novikova.mlkit.graphic.VisionProcessorBase

class FaceDetectionActivity: AppCompatActivity() {
    private val resLayout = R.layout.activity_face_detection

    private var imageUri: Uri? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var preview: ImageView? = null
    private var imageProcessor: VisionProcessorBase<List<Face>>? = null

    private var options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private var detectorOptions = com.novikova.mlkit.faceDetection.FaceDetectorOptions()

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
            val faceDetectorOptions = options
            imageProcessor = FaceDetectorProcessor(this, faceDetectorOptions)
        } catch (e: Exception) {
            toast("Can not create image processor: " + e.message)
        }
    }

    private fun createOptions(
        faceDetectorOptions: com.novikova.mlkit.faceDetection.FaceDetectorOptions
    ) {
        val landmarkMode = if (faceDetectorOptions.isLandmarkAll) {
            FaceDetectorOptions.LANDMARK_MODE_ALL
        } else {
            FaceDetectorOptions.LANDMARK_MODE_NONE
        }
        val performanceMode = if (faceDetectorOptions.isPerformanceFast) {
            FaceDetectorOptions.PERFORMANCE_MODE_FAST
        } else {
            FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
        }
        val classificationMode = if (faceDetectorOptions.isClassificationAll) {
            FaceDetectorOptions.CLASSIFICATION_MODE_ALL
        } else {
            FaceDetectorOptions.CLASSIFICATION_MODE_NONE
        }
        val contourMode = if (faceDetectorOptions.isContourMode) {
            FaceDetectorOptions.CONTOUR_MODE_ALL
        } else {
            FaceDetectorOptions.CONTOUR_MODE_NONE
        }

        val builder = FaceDetectorOptions.Builder()
            .setLandmarkMode(landmarkMode)
            .setPerformanceMode(performanceMode)
            .setClassificationMode(classificationMode)
            .setContourMode(contourMode)

        if (faceDetectorOptions.isEnableTracking) builder.enableTracking()

        faceDetectorOptions.minFaceSize?.let { builder.setMinFaceSize(it) }

        options = builder.build()
        createImageProcessor()
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    private fun initSwitches() {
        findViewById<Switch>(R.id.switchLandmarkMode).setOnCheckedChangeListener { _, isChecked ->
            detectorOptions = if (isChecked) {
                detectorOptions.copy(isLandmarkAll = true)
            } else {
                detectorOptions.copy(isLandmarkAll = false)
            }
            createOptions(detectorOptions)
            tryReloadAndDetectInImage()
        }

        findViewById<Switch>(R.id.switchClassification).setOnCheckedChangeListener { _, isChecked ->
            detectorOptions = if (isChecked) {
                detectorOptions.copy(isClassificationAll = true)
            } else {
                detectorOptions.copy(isClassificationAll = false)
            }
            createOptions(detectorOptions)
            tryReloadAndDetectInImage()
        }

        findViewById<Switch>(R.id.switchContourMode).setOnCheckedChangeListener { _, isChecked ->
            detectorOptions = if (isChecked) {
                detectorOptions.copy(isContourMode = true)
            } else {
                detectorOptions.copy(isContourMode = false)
            }
            createOptions(detectorOptions)
            tryReloadAndDetectInImage()
        }

        findViewById<Switch>(R.id.switchFastAccurate).setOnCheckedChangeListener { _, isChecked ->
            detectorOptions = if (isChecked) {
                detectorOptions.copy(isPerformanceFast = true)
            } else {
                detectorOptions.copy(isPerformanceFast = false)
            }
            createOptions(detectorOptions)
            tryReloadAndDetectInImage()
        }

        findViewById<Switch>(R.id.switchTracking).setOnCheckedChangeListener { _, isChecked ->
            detectorOptions = if (isChecked) {
                detectorOptions.copy(isEnableTracking = true)
            } else {
                detectorOptions.copy(isEnableTracking = false)
            }
            createOptions(detectorOptions)
            tryReloadAndDetectInImage()
        }
    }
}
