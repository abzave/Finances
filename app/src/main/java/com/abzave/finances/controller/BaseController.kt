package com.abzave.finances.controller

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class BaseController {

    /**
     * Get the current date with the format yyyy-mm-dd
     * @return today's date formatted as yyyy-mm-dd
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val now = LocalDateTime.now()
        return formatter.format(now)
    }

}