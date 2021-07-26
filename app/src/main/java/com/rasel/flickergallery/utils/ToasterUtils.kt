package com.rasel.flickergallery.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

object ToasterUtils {

    var toast: Toast? = null

    @SuppressLint("ShowToast")
    fun showToast(context: Context, message: String, length: Int = Toast.LENGTH_SHORT) {
        dismissExistingToast()
        toast = Toast.makeText(context, message, length)
        toast?.show()
    }

    private fun dismissExistingToast() {
        if (toast != null) {
            toast?.cancel()
            toast = null
        }
    }
}