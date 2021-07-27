package com.rasel.flickergallery.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ValidatorTest {

    private val validator = Validator()

    @Test
    fun emailValidationCheck_isValidEmail() {
        val validEmail1 = "rasel@example.com"
        val validEmail2 = "rasel@example.jp"
        val validEmail3 = "rasel@example.co.jp"
        assertEquals(true, validator.validateEmail(validEmail1))
        assertEquals(true, validator.validateEmail(validEmail2))
        assertEquals(true, validator.validateEmail(validEmail3))
    }

    @Test
    fun emailValidationCheck_invalidEmail() {
        val invalidEmail1 = "rasel@example"
        val invalidEmail2 = "rasel@example."
        val invalidEmail3 = "rasel"
        assertEquals(false, validator.validateEmail(invalidEmail1))
        assertEquals(false, validator.validateEmail(invalidEmail2))
        assertEquals(false, validator.validateEmail(invalidEmail3))
        assertEquals(false, validator.validateEmail(null))
        assertEquals(false, validator.validateEmail(""))
        assertEquals(false, validator.validateEmail("ra@rera@ah.com"))
    }
}