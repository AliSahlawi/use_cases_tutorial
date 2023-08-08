package com.example.myapplication.domain.use_case

data class RegistrationUseCases(
    val validateEmail: ValidateEmail = ValidateEmail(),
    val validatePassword: ValidatePassword = ValidatePassword(),
    val validateRepeatedPassword: ValidateRepeatedPassword = ValidateRepeatedPassword(),
    val validateTerms: ValidateTerms = ValidateTerms()
)