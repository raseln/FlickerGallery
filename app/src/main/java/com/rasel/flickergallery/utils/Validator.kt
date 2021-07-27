package com.rasel.flickergallery.utils

import android.util.Patterns

class Validator {

    fun validateEmail(email: String?): Boolean {
        return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}