package com.novikova.mlkit.posedetector

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
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.novikova.mlkit.R
import com.novikova.mlkit.REQUEST_CHOOSE_IMAGE
import com.novikova.mlkit.graphic.BitmapUtils
import com.novikova.mlkit.graphic.GraphicOverlay
import com.novikova.mlkit.graphic.VisionProcessorBase

class PoseDetectionActivity: AppCompatActivity() {
    private val resLayout = R.layout.activity_pose_detection

    private var imageUri: Uri? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var preview: ImageView? = null
    private var imageProcessor: VisionProcessorBase<Pose>? = null
    private var poseDetectorState = PoseDetectorState()

    private var options = PoseDetectorOptions.Builder()
//            STREAM_MODE(default) - search the most prominent person on the first pictures.
//            Then person not searching while camera see him.
//            Use with camera and video stream. If we have many pictures.
//            SINGLE_IMAGE_MODE - search the most prominent person every picture.
//            Use with static image when tracking is not desired.
        .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
        .build()


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

    private fun createImageProcessor() {
        try {
            val poseDetectorOptions = options
            imageProcessor = PoseDetectorProcessor(this, poseDetectorOptions)
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

    private fun initSwitches() {
        findViewById<Switch>(R.id.switchStreamSingle).setOnCheckedChangeListener { _, isChecked ->
            poseDetectorState = if (isChecked) {
                poseDetectorState.copy(isStreamMode = false)
            } else {
                poseDetectorState.copy(isStreamMode = true)
            }
            createOptions(poseDetectorState)
            tryReloadAndDetectInImage()
        }
    }

    private fun createOptions(poseDetectorState: PoseDetectorState) {
        val streamMode = if (poseDetectorState.isStreamMode) {
            PoseDetectorOptions.STREAM_MODE
        } else {
            PoseDetectorOptions.SINGLE_IMAGE_MODE
        }

        options = PoseDetectorOptions.Builder()
            .setDetectorMode(streamMode)
            .build()
        createImageProcessor()
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

}
