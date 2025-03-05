package com.example.navigator.data.model

import dev.romainguy.kotlin.math.Float2

data class DetectedObjectResult(
    val label: String,
    val centerCoordinate: Float2,
)