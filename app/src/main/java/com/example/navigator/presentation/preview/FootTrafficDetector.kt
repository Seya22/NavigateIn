package com.example.navigator.presentation.preview

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.media.Image
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.navigator.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.ar.core.exceptions.NotYetAvailableException
import io.github.sceneview.ar.arcore.ArFrame
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class FootTrafficDetector(private val context: Context) {

    private var isProcessing = false
    private val CHANNEL_ID = "FootTrafficNotificationChannel"
    private val NOTIFICATION_ID = 101

    init {
        createNotificationChannel()
    }

    fun detectFootTraffic(frame: ArFrame) {
        if (isProcessing) return  // Avoid multiple detections at the same time

        isProcessing = true

        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .build()
        val objectDetector = ObjectDetection.getClient(options)

        try {
            Log.e("FootTraffic", "Acquiring camera image for detection...")
            val image = frame.frame.acquireCameraImage()
            if (image == null) {
                Log.e("FootTraffic", "Camera image is null.")
                isProcessing = false
                return
            }
            Log.e("FootTraffic", "Camera image acquired.")

            val bitmap = convertArImageToBitmap(image)
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            Log.e("FootTraffic", "Starting object detection...")
            objectDetector.process(inputImage)
                .addOnSuccessListener { detectedObjects ->
                    Log.e("FootTraffic", "Object detection successful.")
                    Log.e("FootTraffic", "Detected ${detectedObjects.size} objects.")

                    // Count number of people detected
                    val peopleCount = detectedObjects.count { obj ->
                        obj.labels.any { it.text == "Person" }
                    }

                    // Send notification based on people count
                    when {
                        peopleCount in 0..1 -> {
                            sendNotification("Light Foot Traffic Detected")
                        }
                        peopleCount in 3..5 -> {
                            sendNotification("Medium Foot Traffic Detected")
                        }
                        peopleCount > 5 -> {
                            sendNotification("Intense Foot Traffic")
                        }
                    }

                    isProcessing = false
                }
                .addOnFailureListener { e ->
                    Log.e("FootTraffic", "Error detecting objects", e)
                    isProcessing = false
                }
            image.close()
        } catch (e: NotYetAvailableException) {
            Log.e("FootTraffic", "AR Frame not available yet")
            isProcessing = false
        } catch (e: Exception) {
            Log.e("FootTraffic", "Exception in detectFootTraffic", e)
            isProcessing = false
        }
    }

    private fun convertArImageToBitmap(image: Image): Bitmap {
        Log.e("FootTraffic", "Converting AR image to Bitmap...")
        val yBuffer: ByteBuffer = image.planes[0].buffer
        val uBuffer: ByteBuffer = image.planes[1].buffer
        val vBuffer: ByteBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, image.width, image.height), 90, out)
        val imageBytes = out.toByteArray()

        val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Log.e("FootTraffic", "Bitmap conversion complete.")
        return bitmap
    }

    private fun sendNotification(message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Use your app's icon
            .setContentTitle("Foot Traffic Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Foot Traffic Alerts"
            val descriptionText = "Notifications for foot traffic detection"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
