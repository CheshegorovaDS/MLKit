package com.novikova.mlkit.posedetector

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.novikova.mlkit.graphic.GraphicOverlay
import com.novikova.mlkit.graphic.VisionProcessorBase
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/** A processor to run pose detector. */
class PoseDetectorProcessor(
  context: Context,
  options: PoseDetectorOptions
) : VisionProcessorBase<Pose>(context) {

  private val detector: PoseDetector = PoseDetection.getClient(options)

  override fun stop() {
    detector.close()
  }

  override fun detectInImage(image: InputImage): Task<Pose> {
    return detector.process(image)
  }

  override fun onFailure(e: Exception) {
    Log.e(TAG, "Pose detection failed!", e)
  }

  override fun onSuccess(results: Pose, graphicOverlay: GraphicOverlay) = graphicOverlay.add(
      PoseGraphic(graphicOverlay, results)
  )

  companion object {
    private const val TAG = "PoseDetectorProcessor"
  }

}
