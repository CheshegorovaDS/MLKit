package com.novikova.mlkit.faceDetection

data class FaceDetectorOptions(
    val isLandmarkAll: Boolean = true,
    val isPerformanceFast: Boolean = true,
    val isClassificationAll: Boolean = true,
    val isContourMode: Boolean = true,
    val minFaceSize: Float? = null,
    val isEnableTracking: Boolean = false
)
