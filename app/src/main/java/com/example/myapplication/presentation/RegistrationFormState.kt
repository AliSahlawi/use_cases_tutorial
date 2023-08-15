package com.example.myapplication.presentation

data class RegistrationFormState (
    val email : String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val repeatedPassword: String = "",
    val repeatedPasswordError: String? = null,
    val acceptedTerms: Boolean = false,
    val termsError: String? = null,
    val billers: List<Biller>? = null,
    val services: List<ServiceDetail> ? = null,
    val selectedBiller: Biller ? =null,
    val selectedServiceDetail: ServiceDetail? = null
        )