package dev.dejoe.nougall

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {
    fun formatDate(dateStr: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            val date = LocalDate.parse(dateStr, inputFormatter)
            outputFormatter.format(date)
        } catch (e: Exception) {
            "-"
        }
    }
    fun List<Int>.toCommaSeparated(): String =
        joinToString(",")

    fun String.toIntList(): List<Int> =
        if (isBlank()) emptyList()
        else split(",").map { it.toInt() }
}

