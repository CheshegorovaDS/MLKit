package com.novikova.mlkit.graphic

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.odml.image.MlImage
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(VisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
abstract class VisionProcessorBase<T>(context: Context) :
  VisionImageProcessor<T> {

  companion object {
    const val MANUAL_TESTING_LOG = "LogTagForTest"
    private const val TAG = "VisionProcessorBase"
  }

  override fun detectImage(bitmap: Bitmap?, graphicOverlay: GraphicOverlay?): Task<T>? {
    return detectInImage(InputImage.fromBitmap(bitmap!!, 0))
  }

  abstract fun detectInImage(image: InputImage): Task<T>?

  protected open fun detectInImage(image: MlImage): Task<T> {
    return Tasks.forException(
      MlKitException(
        "MlImage is currently not demonstrated for this feature",
        MlKitException.INVALID_ARGUMENT
      )
    )
  }

  abstract fun onSuccess(results: T, graphicOverlay: GraphicOverlay)

  protected abstract fun onFailure(e: Exception)

}
