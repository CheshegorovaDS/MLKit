package com.novikova.mlkit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.novikova.mlkit.faceDetection.FaceDetectionActivity
import com.novikova.mlkit.faceDetection.SimpleFaceDetectionActivity
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    private val map = mutableMapOf<String, Class<*>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMap()

        findViewById<EditText>(R.id.detection).setOnClickListener {
            showDetectionDialog()
        }

        findViewById<Button>(R.id.detect).setOnClickListener {
            //select image
            val currentDetection = findViewById<EditText>(R.id.detection).text.toString()
            val intent = Intent(
                applicationContext,
                map[currentDetection]
            )
            startActivity(intent)
        }
    }

    private fun initMap() {
        map[getString(R.string.face_detection)] =
            Class.forName("com.novikova.mlkit.faceDetection.FaceDetectionActivity")
        map[getString(R.string.object_detection)] =
            Class.forName("com.novikova.mlkit.objectDetectionAndTracking.ObjectDetectionActivity")
        map[getString(R.string.pose_detection)] =
            Class.forName("com.novikova.mlkit.posedetector.PoseDetectionActivity")
        map[getString(R.string.selfie_segmentation)] =
            Class.forName("com.novikova.mlkit.selfiesegmentation.SegmentationActivity")
        map[getString(R.string.text_detection)] =
            Class.forName("com.novikova.mlkit.textdetector.TextDetectionActivity")
    }

    private fun showDetectionDialog() {
        val array = resources.getStringArray(R.array.detection)
        var detection = getString(R.string.face_detection)

        val builder = createAlertDialog(
            resources.getString(R.string.choose_detection)
        )

        builder.setPositiveButton(
            resources.getString(R.string.ok)
        ){ _, _ ->
            findViewById<EditText>(R.id.detection).setText(detection)
        }
            .setSingleChoiceItems(array, 0) { _, index ->
                detection = array[index]
            }

        builder.show()
    }

    private fun createAlertDialog(
        title: String
    ): AlertDialog.Builder =
        AlertDialog.Builder(this)
            .setTitle(title)
            .setCancelable(true)
            .setNegativeButton(
                resources.getString(R.string.cancel)
            ){ dialog, _ -> dialog.cancel() }

}