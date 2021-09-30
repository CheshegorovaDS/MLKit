package com.novikova.mlkit.textdetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import com.google.mlkit.vision.text.Text
import com.novikova.mlkit.graphic.GraphicOverlay
import kotlin.math.max
import kotlin.math.min

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextGraphic
constructor(
  overlay: GraphicOverlay?,
  private val text: Text
) : GraphicOverlay.Graphic(overlay) {

  private val rectPaint: Paint = Paint()
  private val textPaint: Paint
  private val labelPaint: Paint

  init {
    rectPaint.color = MARKER_COLOR
    rectPaint.style = Paint.Style.STROKE
    rectPaint.strokeWidth = STROKE_WIDTH
    textPaint = Paint()
    textPaint.color = TEXT_COLOR
    textPaint.textSize = TEXT_SIZE
    labelPaint = Paint()
    labelPaint.color = MARKER_COLOR
    labelPaint.style = Paint.Style.FILL
    // Redraw the overlay, as this graphic has been added.
    postInvalidate()
  }

  /** Draws the text block annotations for position, size, and raw value on the supplied canvas. */
  override fun draw(canvas: Canvas) {
    Log.d(TAG, "Text is: " + text.text)
    for (textBlock in text.textBlocks) { // Renders the text at the bottom of the box.
        for (line in textBlock.lines) {
          // Draws the bounding box around the TextBlock.
          val rect = RectF(line.boundingBox)
          drawText(line.text, rect, TEXT_SIZE + 2 * STROKE_WIDTH, canvas)
          for (element in line.elements) {
            Log.d(TAG, "Element text is: " + element.text)
            Log.d(TAG, "Element language is: " + element.recognizedLanguage)
          }
        }
    }
  }

  private fun drawText(text: String, rect: RectF, textHeight: Float, canvas: Canvas) {
    // If the image is flipped, the left will be translated to right, and the right to left.
    val x0 = translateX(rect.left)
    val x1 = translateX(rect.right)
    rect.left = min(x0, x1)
    rect.right = max(x0, x1)
    rect.top = translateY(rect.top)
    rect.bottom = translateY(rect.bottom)
    canvas.drawRect(rect, rectPaint)
    val textWidth = textPaint.measureText(text)
    canvas.drawRect(
      rect.left - STROKE_WIDTH,
      rect.top - textHeight,
      rect.left + textWidth + 2 * STROKE_WIDTH,
      rect.top,
      labelPaint
    )
    // Renders the text at the bottom of the box.
    canvas.drawText(text, rect.left, rect.top - STROKE_WIDTH, textPaint)
  }

  companion object {
    private const val TAG = "TextGraphic"
    private const val TEXT_COLOR = Color.BLACK
    private const val MARKER_COLOR = Color.WHITE
    private const val TEXT_SIZE = 54.0f
    private const val STROKE_WIDTH = 4.0f
  }
}
