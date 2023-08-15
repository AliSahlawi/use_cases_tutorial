package com.example.myapplication.presentation

sealed class RegistrationFormEvent {
    data class EmailChanged(val email: String) : RegistrationFormEvent()
    data class PasswordChanged(val password: String) : RegistrationFormEvent()
    data class RepeatedPasswordChanged(
        val repeatedPassword: String
    ) : RegistrationFormEvent()

    data class AcceptTerms(val isAccepted: Boolean) : RegistrationFormEvent()

    data class OnBillerSelected(val biller: Biller): RegistrationFormEvent()
    data class OnServiceSelected(val service: ServiceDetail): RegistrationFormEvent()
    object Submit: RegistrationFormEvent()
}