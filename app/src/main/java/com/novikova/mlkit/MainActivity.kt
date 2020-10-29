package com.novikova.mlkit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity() {

    companion object{
        const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.setOnClickListener {
            if (hasCameraPermission()) {
                scanBarcode()
            } else {
                getPermission()
            }
        }
    }

    private fun getPermission() {
        EasyPermissions.requestPermissions(this, "Необходим доступ к камере",
            CAMERA_PERMISSION_REQUEST_CODE, Manifest.permission.CAMERA
        )
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(CAMERA_PERMISSION_REQUEST_CODE)
    private fun scanBarcode() {
        startActivity(Intent(this, ScannerBarcodeActivity::class.java))
    }
}
