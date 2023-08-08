package com.example.myapplication.presentation


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.use_case.ValidateEmail
import com.example.myapplication.domain.use_case.ValidatePassword
import com.example.myapplication.domain.use_case.ValidateRepeatedPassword
import com.example.myapplication.domain.use_case.ValidateTerms
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validatePassword: ValidatePassword = ValidatePassword(),
    private val validateRepeatedPassword: ValidateRepeatedPassword = ValidateRepeatedPassword(),
    private val validateTerms: ValidateTerms = ValidateTerms()
) : ViewModel() {

    private var _registrationFormState = MutableLiveData(RegistrationFormState())
    val registrationFormState: LiveData<RegistrationFormState> = _registrationFormState

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()


    fun onEvent(event: RegistrationFormEvent){
        val currentState = _registrationFormState.value ?: RegistrationFormState()
        when(event){
            is RegistrationFormEvent.AcceptTerms -> {
                _registrationFormState.value = currentState.copy(acceptedTerms = event.isAccepted)
            }
            is RegistrationFormEvent.EmailChanged -> {
                _registrationFormState.value = currentState.copy( email = event.email)
            }
            is RegistrationFormEvent.PasswordChanged -> {
                _registrationFormState.value = currentState.copy(password = event.password)
            }
            is RegistrationFormEvent.RepeatedPasswordChanged -> {
                _registrationFormState.value = currentState.copy(repeatedPassword = event.repeatedPassword)
            }
            RegistrationFormEvent.Submit -> submitData()
        }
    }

    private fun submitData() {
        val emailResult = validateEmail.execute(_registrationFormState.value!!.email)
        val passwordResult = validatePassword.execute(_registrationFormState.value!!.password)
        val repeatedPasswordResult = validateRepeatedPassword.execute(
            _registrationFormState.value!!.password, _registrationFormState.value!!.repeatedPassword
        )
        val termsResult = validateTerms.execute(_registrationFormState.value!!.acceptedTerms)

        val hasError = listOf(
            emailResult,
            passwordResult,
            repeatedPasswordResult,
            termsResult
        ).any { !it.successful }
        _registrationFormState.value = _registrationFormState.value!!.copy(
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