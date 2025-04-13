package com.example.navigator.data.ml.classification

import android.graphics.Bitmap
import android.graphics.Rect
import android.media.Image
import com.example.navigator.data.ml.classification.utils.ImageUtils
import com.example.navigator.data.model.DetectedObjectResult
import com.example.navigator.domain.ml.ObjectDetector
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dev.romainguy.kotlin.math.Float2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TextAnalyzer : ObjectDetector {

    private val options = TextRecognizerOptions.Builder().build()
    private val detector = TextRecognition.getClient(options)

    override suspend fun analyze(
        mediaImage: Image,
        rotationDegrees: Int,
        imageCropPercentages: Pair<Int, Int>,
        displaySize: Pair<Int, Int>
    ): Result<DetectedObjectResult> {

        var text: Text?
        var cropRect: Rect?
        var croppedBit: Bitmap?

        withContext(Dispatchers.Default) {
            val imageHeight = mediaImage.height
            val imageWidth = mediaImage.width
            val actualAspectRatio = imageWidth / imageHeight

            val convertImageToBitmap = ImageUtils.convertYuv420888ImageToBitmap(mediaImage)
            cropRect = Rect(0, 0, imageWidth, imageHeight)

            var currentCropPercentages = imageCropPercentages
            if (actualAspectRatio > 3) {
                currentCropPercentages = Pair(
                    currentCropPercentages.first / 2,
                    currentCropPercentages.second
                )
            }

            val heightCropPercent = currentCropPercentages.first
            val widthCropPercent = currentCropPercentages.second
            val (widthCrop, heightCrop) = when (rotationDegrees) {
                90, 270 -> Pair(heightCropPercent / 100f, widthCropPercent / 100f)
                else -> Pair(widthCropPercent / 100f, heightCropPercent / 100f)
            }

            cropRect!!.inset(
                (imageWidth * widthCrop / 2).toInt(),
                (imageHeight * heightCrop / 2).toInt()
            )

            val croppedBitmap = ImageUtils.rotateAndCrop(convertImageToBitmap, rotationDegrees, cropRect!!)
            croppedBit = croppedBitmap

            text = detector.process(InputImage.fromBitmap(croppedBitmap, 0)).await()
        }

        return if (text != null && text!!.textBlocks.isNotEmpty()) {

            // ✅ Find the best matching element
            val match = text!!.textBlocks
                .flatMap { it.lines }
                .flatMap { it.elements }
                .firstOrNull { filterFunction(it.text) }

            if (match != null && match.boundingBox != null) {
                val boundingBox = match.boundingBox!!
                val croppedRatio = Float2(
                    boundingBox.centerX() / croppedBit!!.width.toFloat(),
                    boundingBox.centerY() / croppedBit!!.height.toFloat()
                )

                val x = displaySize.first * croppedRatio.x
                val y = displaySize.second * croppedRatio.y

                Result.success(
                    DetectedObjectResult(
                        label = match.text,
                        centerCoordinate = Float2(x, y)
                    )
                )
            } else {
                Result.failure(Exception("No valid room number found"))
            }
        } else {
            Result.failure(Exception("No detected text"))
        }
    }

    // ✅ Flexible regex: matches things like 5LA, A123, B3A, 101, etc.
    private fun filterFunction(text: String): Boolean {
        val normalized = text.trim().uppercase()
        val roomNumberPattern = Regex("^[A-Z]{0,3}\\d{1,4}[A-Z]{0,2}$")
        return roomNumberPattern.matches(normalized)
    }
}
