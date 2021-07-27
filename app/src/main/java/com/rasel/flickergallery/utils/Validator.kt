package com.rasel.flickergallery.utils

import androidx.core.util.PatternsCompat

class Validator {

    fun validateEmail(email: String?): Boolean {
        return !email.isNullOrEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }
}