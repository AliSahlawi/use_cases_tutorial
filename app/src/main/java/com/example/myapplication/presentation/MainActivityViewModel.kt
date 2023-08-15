package com.example.myapplication.presentation


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.use_case.ValidateEmail
import com.example.myapplication.domain.use_case.ValidatePassword
import com.example.myapplication.domain.use_case.ValidateRepeatedPassword
import com.example.myapplication.domain.use_case.ValidateTerms
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Random
import java.util.RandomAccess

class MainActivityViewModel(
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validatePassword: ValidatePassword = ValidatePassword(),
    private val validateRepeatedPassword: ValidateRepeatedPassword = ValidateRepeatedPassword(),
    private val validateTerms: ValidateTerms = ValidateTerms()
) : ViewModel() {



    var state by  mutableStateOf(RegistrationFormState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()


    fun onEvent(event: RegistrationFormEvent){
        val currentState = state
        when(event){
            is RegistrationFormEvent.AcceptTerms -> {
                state = currentState.copy(acceptedTerms = event.isAccepted)
            }
            is RegistrationFormEvent.EmailChanged -> {
                state = currentState.copy( email = event.email)

            }
            is RegistrationFormEvent.PasswordChanged -> {
                state = currentState.copy(password = event.password)
            }
            is RegistrationFormEvent.RepeatedPasswordChanged -> {
                state = currentState.copy(repeatedPassword = event.repeatedPassword)
            }
            is RegistrationFormEvent.OnBillerSelected -> {
                val services = listOf(ServiceDetail("Fawri","FAWRI","AHM"),ServiceDetail("Fawri+","FP","FP1546"))
                val services1 = listOf(ServiceDetail("Fawri","FAWRI","AHM"))
                val actualService = listOf(services,services1)[(0..1).random()]
                state = state.copy(selectedBiller = event.biller,services = actualService)

            }
            is RegistrationFormEvent.OnServiceSelected -> {
                state = state.copy(selectedServiceDetail = event.service)
            }
            is RegistrationFormEvent.Submit -> submitData()
        }
    }

    private fun submitData() {
        val emailResult = validateEmail.execute(state.email)
        val passwordResult = validatePassword.execute(state.password)
        val repeatedPasswordResult = validateRepeatedPassword.execute(
            state.password, state.repeatedPassword
        )
        val termsResult = validateTerms.execute(state.acceptedTerms)

        val hasError = listOf(
            emailResult,
            passwordResult,
            repeatedPasswordResult,
            termsResult
        ).any { !it.successful }
        state = state.copy(
            emailError = emailResult.errorMessage,
            passwordError = passwordResult.errorMessage,
            repeatedPasswordError = repeatedPasswordResult.errorMessage,
            termsError = termsResult.errorMessage
        )
        state = state.copy(
            emailError = emailResult.errorMessage,
            passwordError = passwordResult.errorMessage,
            repeatedPasswordError = repeatedPasswordResult.errorMessage,
            termsError = termsResult.errorMessage
        )
        if (hasError){
            return
        }
        viewModelScope.launch {
            validationEventChannel.send(ValidationEvent.Success)
        }
    }





    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }



}