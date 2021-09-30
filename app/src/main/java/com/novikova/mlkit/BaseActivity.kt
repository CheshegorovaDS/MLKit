package com.novikova.mlkit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.novikova.mlkit.faceDetection.SourceInputImage

open class BaseActivity: AppCompatActivity() {

    protected fun createInputImage(source: SourceInputImage, imageView: ImageView): InputImage {
        val inputImage = when (source) {
            SourceInputImage.BITMAP -> setImageFromBitmap(imageView)
//            SourceInputImage.URI -> setImageFromUri()
            else -> setImageFromBitmap(imageView)
        }
        return inputImage
    }

    private fun setImageFromUri(): InputImage {
        val uri = Uri.parse("")
        return  InputImage.fromFilePath(this, uri)
    }

    private fun setImageFromBitmap(imageView: ImageView): InputImage {
        val bitmap = getBitmapFromAssets()
        imageView.setImageBitmap(bitmap)
        return InputImage.fromBitmap(bitmap, 0)
    }

    private fun getBitmapFromAssets(): Bitmap {
        val assetManager = assets
        val instr = assetManager.open("photo.jpg")
        val bitmap = BitmapFactory.decodeStream(instr)
        instr.close()
        return bitmap
    }

    protected fun showException(message: String?) =
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

    protected fun createAlertDialog(title: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setPositiveButton("Ok") { _, _ -> }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    protected fun setIsProgressBarVisible(isVisible: Boolean) {
        findViewById<ProgressBar>(R.id.progressBar).visibility = when (isVisible) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }

}