package com.novikova.mlkit.graphic;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.novikova.mlkit.graphic.GraphicOverlay;

/** An interface to process the images with different vision detectors and custom image models. */
public interface VisionImageProcessor<T> {

  Task<T> detectImage(Bitmap bitmap, GraphicOverlay graphicOverlay);

  /** Stops the underlying machine learning model and release resources. */
  void stop();
}
