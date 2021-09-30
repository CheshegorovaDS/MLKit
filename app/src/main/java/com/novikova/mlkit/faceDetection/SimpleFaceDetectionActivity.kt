package com.novikova.mlkit.faceDetection

import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.novikova.mlkit.BaseActivity
import com.novikova.mlkit.R

class SimpleFaceDetectionActivity: BaseActivity() {

    private val resLayout = R.layout.activity_simple_face_detection

//    1.Создание детектора
    private val options = FaceDetectorOptions.Builder()
        //важнее скорость или точность
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        //нужно ли определять глаза, уши, ...
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        // следует ли классифицировать по категориям улыбается, открыты глаза
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
        //наим желаемый размер лица
        // setMinFaceSize
        //Следует ли назначать лицам идентификатор, который можно использовать для отслеживания лиц на изображениях.
        //enableTracking

    // Real-time contour detection
    val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resLayout)

        passImage()
        findViewById<MaterialButton>(R.id.showSmiles).setOnClickListener {
            showSmiles()
        }
    }

//       2.Создание InputImage
    private val source = SourceInputImage.BITMAP

//    3. Получить FaceDetector

    private fun getFaceDetector(): FaceDetector {
        val detector = FaceDetection.getClient(options)
        //Default Detector = FaceDetection.getClient()
        return detector
    }

//    4. Обработать изображение

    private fun passImage() {
        val image = createInputImage(source, findViewById(R.id.imageView))
        val detector = getFaceDetector()

        setIsProgressBarVisible(true)
        detector.process(image)
            .addOnSuccessListener { faces ->
                setIsProgressBarVisible(false)
//                setButtonEnabled(true)
                onSuccessDetection(faces)
            }
            .addOnFailureListener { e ->
                setIsProgressBarVisible(false)
                showException(e.localizedMessage)
            }
    }

//    5. Получение информации о лицах.
    private var faces = mutableListOf<Face>()
    private fun onSuccessDetection(faces: List<Face>) {
        //faces - список лиц, найденных на изображении
        showCountFaces(faces)
        showCountSmiledFaces(faces)
        this.faces.addAll(faces)
    }

//      6. Анализ лиц.
      private fun showCountFaces(faces: List<Face>) {
            createAlertDialog("Количество лиц = ${faces.size}")
      }

    private fun showCountSmiledFaces(faces: List<Face>) {
        var count = 0
        for (face in faces) {
            if (face.smilingProbability != null) {
                count ++
            }
        }

        createAlertDialog("Количество улыбающихся = $count")
    }

    private fun showSmiles() {
        for (face in faces) {
            if (face.smilingProbability != null) {
                val smile = face.smilingProbability
            }
        }
    }
}
