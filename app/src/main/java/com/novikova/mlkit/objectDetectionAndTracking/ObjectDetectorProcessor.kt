package com.novikova.mlkit.objectDetectionAndTracking

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.novikova.mlkit.graphic.GraphicOverlay
import com.novikova.mlkit.graphic.VisionProcessorBase
import java.io.IOException

/** A processor to run object detector.  */
class ObjectDetectorProcessor(context: Context, options: ObjectDetectorOptions) :
  VisionProcessorBase<List<DetectedObject>>(context) {

  private val detector: ObjectDetector = ObjectDetection.getClient(options)

  override fun stop() {
    try {
      detector.close()
    } catch (e: IOException) {
      Log.e(
        TAG,
        "Exception thrown while trying to close object detector!",
        e
      )
    }
  }

  override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
    return detector.process(image)
  }

  override fun onSuccess(results: List<DetectedObject>, graphicOverlay: GraphicOverlay) {
    for (result in results) {
      graphicOverlay.add(ObjectGraphic(graphicOverlay, result))
    }
  }

  override fun onFailure(e: Exception) {
    Log.e(TAG, "Object detection failed!", e)
  }

  companion object {
    private const val TAG = "ObjectDetectorProcessor"
  }
}
