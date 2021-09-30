package com.novikova.mlkit.objectDetectionAndTracking

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.novikova.mlkit.BaseActivity
import com.novikova.mlkit.R
import com.novikova.mlkit.faceDetection.SourceInputImage

class SimpleObjectDetectionActivity: BaseActivity() {
    private val resLayout = R.layout.activity_object_detection_and_tracking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resLayout)

        passImage()
    }

//    1. Create detector
    private val options = ObjectDetectorOptions.Builder()
        //ObjectDetectorOptions.STREAM_MODE - ставятся идентификаторы на объекты,
    //не определяется рамка объекта, результат может быть не полным, маленькая задержка
        //SINGLE_IMAGE_MODE - изображение не обработается, пока не орпределиться рамка,
        //не назначаются идент отслеживания, полный анализ
     .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        //классифицировать ли объекты по 5 категориям:
    //место, еда, растение, одежда, товары
    .enableClassification()  // Optional
        //отслеживание неско-их объектов
        //обнаруживает до 5 объектов
    .enableMultipleObjects()
    .build()

//    2. Get Detector
    private fun getDetector(): ObjectDetector = ObjectDetection.getClient(options)

//    3. Get ImageInput
    private val source = SourceInputImage.URI

//    4. Pass image

    private fun passImage() {
        val image = createInputImage(source, findViewById(R.id.imageView2))
        val detector = getDetector()

        setIsProgressBarVisible(true)
        detector.process(image)
            .addOnSuccessListener { detectedObjects ->
                setIsProgressBarVisible(false)
//                setButtonEnabled(true)
                onSuccessDetection(detectedObjects)
            }
            .addOnFailureListener { e ->
                setIsProgressBarVisible(false)
                showException(e.localizedMessage)
            }
    }

    private fun onSuccessDetection(objects: List<DetectedObject>) {
        val bitmap = (findViewById<ImageView>(R.id.imageView2).drawable as BitmapDrawable).bitmap
        val canvas = Canvas(bitmap)
        for (detectedObject in objects) {
            val bounds: Rect = detectedObject.boundingBox
//            val paint = Paint()
//            paint.color = Color.GREEN
//            canvas.drawRect(bounds, paint)
        }
    }

}