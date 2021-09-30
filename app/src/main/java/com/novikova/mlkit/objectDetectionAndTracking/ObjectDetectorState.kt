package com.novikova.mlkit.objectDetectionAndTracking

data class ObjectDetectorState(
    val isStreamMode: Boolean = true,
    val isEnableClassification: Boolean = false,
    val isEnableMultipleObjects: Boolean = false,
)
