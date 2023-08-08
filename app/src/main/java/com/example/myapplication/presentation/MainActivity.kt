package com.example.myapplication.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.validationEvents.collect { event ->
                when (event) {
                    is MainActivityViewModel.ValidationEvent.Success -> {
                        Toast.makeText(
                            applicationContext,
                            "Successful!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        viewModel.registrationFormState.observe(this) { state ->
            updateUI(state)
        }
    }

    private fun configureViews() {
        with(binding) {
            val state = viewModel.registrationFormState.value

            if (state != null) {
                emailEditText.apply {
                    setText(state.email)
                    doAfterTextChanged { text ->
                        viewModel.onEvent(RegistrationFormEvent.EmailChanged(text.toString()))
                    }
                }

                passwordEditText.apply {
                    setText(state.password)
                    doAfterTextChanged { text ->
                        viewModel.onEvent(RegistrationFormEvent.PasswordChanged(text.toString()))
                    }
                }

                repeatPasswordEditText.apply {
                    setText(state.repeatedPassword)
                    doAfterTextChanged { text ->
                        viewModel.onEvent(RegistrationFormEvent.RepeatedPasswordChanged(text.toString()))
                    }
                }

                termsCheckBox.apply {
                    setOnCheckedChangeListener { _, isChecked ->
                        viewModel.onEvent(RegistrationFormEvent.AcceptTerms(isChecked))
                    }
                    isChecked = state.acceptedTerms
                }

                submitButton.setOnClickListener {
                    viewModel.onEvent(RegistrationFormEvent.Submit)
                }
            }
        }
    }

    private fun updateUI(state: RegistrationFormState) {
        with(binding) {
            emailErrorTextView.apply {
                visibility = if (state.emailError != null) View.VISIBLE else View.GONE
                text = state.emailError
            }

            passwordErrorTextView.apply {
                visibility = if (state.passwordError != null) View.VISIBLE else View.GONE
                text = state.passwordError
            }

            repeatPasswordErrorTextView.apply {
                visibility = if (state.repeatedPasswordError != null) View.VISIBLE else View.GONE
                text = state.repeatedPasswordError
            }

            termsErrorTextView.apply {
                visibility = if (state.termsError != null) View.VISIBLE else View.GONE
                text = state.termsError
            }
        }
    }
}

