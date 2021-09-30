package com.novikova.mlkit.selfiesegmentation

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.novikova.mlkit.graphic.GraphicOverlay
import com.novikova.mlkit.graphic.VisionProcessorBase

/** A processor to run Segmenter.  */
class SegmenterProcessor(context: Context, options: SelfieSegmenterOptions) :
  VisionProcessorBase<SegmentationMask>(context) {

  private val segmenter: Segmenter = Segmentation.getClient(options)

  override fun detectInImage(image: InputImage): Task<SegmentationMask> {
    return segmenter.process(image)
  }

  override fun onSuccess(
    segmentationMask: SegmentationMask,
    graphicOverlay: GraphicOverlay
  ) {
    graphicOverlay.add(
      SegmentationGraphic(
        graphicOverlay,
        segmentationMask
      )
    )
  }

  override fun onFailure(e: Exception) {
    Log.e(TAG, "Segmentation failed: $e")
  }

  override fun stop() = segmenter.close()

  companion object {
    private const val TAG = "SegmenterProcessor"
  }
}
