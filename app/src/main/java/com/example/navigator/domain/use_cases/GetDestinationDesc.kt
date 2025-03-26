package com.example.navigator.domain.use_cases

import android.content.Context
import com.example.navigator.R

class GetDestinationDesc {

    operator fun invoke(number: String, context: Context): String {
        var building = ""
        val floorStr = context.getString(R.string.floor)

        // Extract the floor number (first character is always a digit)
        val floor = number[0].digitToInt()

        // Determine building based on length and content
        when {
            number.length == 1 -> {
                building = context.getString(R.string.main)
            }
            number.length == 2 || number.length == 3 -> {
                val secondLastChar = number.getOrNull(number.length - 2) ?: '0'
                building = if (secondLastChar.isDigit() && secondLastChar.digitToInt() > 4) {
                    context.getString(R.string.lab)
                } else {
                    context.getString(R.string.main)
                }
            }
            number.length == 4 -> {
                building = when (number[0]) {
                    '1' -> context.getString(R.string.first_tower)
                    '2' -> context.getString(R.string.second_tower)
                    '3' -> context.getString(R.string.second_building)
                    else -> context.getString(R.string.main) // Default case for safety
                }
            }
            else -> {
                return "" // Invalid number format, return empty
            }
        }

        return "$building, $floorStr$floor"
    }

}