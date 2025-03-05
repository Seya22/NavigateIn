package com.example.navigator.data.ml.classification

import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await

class FaceAnalyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setMinFaceSize(0.15f) // Adjust sensitivity
        .build()

    private val detector = FaceDetection.getClient(options)

    suspend fun analyze(image: Image, rotationDegrees: Int): Boolean {
        return try {
            val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
            val faces = detector.process(inputImage).await()
            Log.d("FaceAnalyzer", "Faces Detected: ${faces.size}")
            return faces.size >= 2 // Return true if 2+ faces are found
        } catch (e: Exception) {
            Log.e("FaceAnalyzer", "Face detection error: ${e.message}")
            false
        }
    }
}
