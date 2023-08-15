package com.example.myapplication.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.screen.setContent {
            Screen()
        }
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
    }

    private fun configureViews() {
        val state = viewModel.state
            with(binding) {


            emailEditText.setContent {
                EmailInputField()
            }

                passwordEditText.apply {
                    setText(state.password)
                    doAfterTextChanged { text ->
                        viewModel?.onEvent(RegistrationFormEvent.PasswordChanged(text.toString()))
                    }
                }

                repeatPasswordEditText.apply {
                    setText(state.repeatedPassword)
                    doAfterTextChanged { text ->
                        viewModel?.onEvent(RegistrationFormEvent.RepeatedPasswordChanged(text.toString()))
                    }
                }

                termsCheckBox.apply {
                    setOnCheckedChangeListener { _, isChecked ->
                        viewModel?.onEvent(RegistrationFormEvent.AcceptTerms(isChecked))
                    }
                    isChecked = state.acceptedTerms
                }

                submitButton.setOnClickListener {
                    viewModel?.onEvent(RegistrationFormEvent.Submit)
                }

                passwordErrorTextView.apply {
                    visibility = if (state.passwordError != null) View.VISIBLE else View.GONE
                    text = state.passwordError
                }

                repeatPasswordErrorTextView.apply {
                    visibility =
                        if (state.repeatedPasswordError != null) View.VISIBLE else View.GONE
                    text = state.repeatedPasswordError
                }

                termsErrorTextView.apply {
                    visibility = if (state.termsError != null) View.VISIBLE else View.GONE
                    text = state.termsError
                }

                binding.radioGroup.setContent {
                RadioButtonSample()
            }
        }
    }

    @Composable
    fun RadioButtonSample() {
        val radioOptions = listOf("Fawri", "Fawri +")

        var selectedItem by remember {
            mutableStateOf(radioOptions[0])
        }

        Row(modifier = Modifier.selectableGroup()) {
            radioOptions.forEach { label ->
                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .selectable(
                            selected = (selectedItem == label),
                            onClick = { selectedItem = label },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        modifier = Modifier.padding(end = 16.dp),
                        selected = (selectedItem == label),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(text = label)
                }
            }
        }
    }


    @Composable
    fun EmailInputField() {
        val state = viewModel.state

        Column {
            TextField(
                value = state.email,
                onValueChange = { text ->
                    viewModel.onEvent(RegistrationFormEvent.EmailChanged(text))
                },
                isError = state.emailError != null,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (state.emailError != null) {
                Text(text = state.emailError, color = Color.Red)
            }
        }
    }

    @Composable
    fun InputField(
        value: String,
        onValueChanged: (String) -> Unit,
        hint: String,
        modifier: Modifier = Modifier,
    ) {
        var isClearVisible by remember {
            mutableStateOf(false)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .align(Alignment.Bottom)
            )
            TextField(
                value = value,
                modifier = modifier
                    .onFocusChanged {
                        isClearVisible = it.hasFocus
                    },
                onValueChange = onValueChanged,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray
                ),
                trailingIcon = {
                    if (isClearVisible) {
                        IconButton(

                            onClick = { onValueChanged("") },
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }

                },
                label = { Text(text = hint) }

            )
        }
    }


    // Creating a composable function
// to create an Outlined Text Field
// Calling this function as content
// in the above function
    @Composable
    fun Spinner(billers: List<Biller>) {
        var billerName by remember {
            mutableStateOf("Select Biller")
        }
        var expanded by remember {
            mutableStateOf(false)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "")
                    Text(text = billerName, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp))
                }

                Spacer(modifier = Modifier.weight(1f)) // Add a spacer to push the dropdown menu to the end

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier

                        .fillMaxWidth() // Make the dropdown menu fill the whole width
                ) {
                    billers.forEach { biller ->
                        DropdownMenuItem(
                            text = { Text(biller.billerName) },
                            onClick = {
                                expanded = false
                                billerName = biller.billerName
                                viewModel.onEvent(RegistrationFormEvent.OnBillerSelected(biller))
                            },
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .fillMaxWidth() // Make each item fill the whole width
                        )
                    }
                }

                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
            }

        }
    }
    @Composable
    fun ServicesSpinner(services: List<ServiceDetail>) {
        var serviceName by remember {
            mutableStateOf("Select Service")
        }
        var expanded by remember {
            mutableStateOf(false)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "")
                    Text(text = serviceName, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp))
                }

                Spacer(modifier = Modifier.weight(1f)) // Add a spacer to push the dropdown menu to the end

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier

                        .fillMaxWidth() // Make the dropdown menu fill the whole width
                ) {
                    services.forEach { service ->
                        DropdownMenuItem(
                            text = { Text(service.serviceName) },
                            onClick = {
                                expanded = false
                                serviceName = service.serviceName
                                viewModel.onEvent(RegistrationFormEvent.OnServiceSelected(service))
                            },
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .fillMaxWidth() // Make each item fill the whole width
                        )
                    }
                }

                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
            }

        }
    }

    @Composable
    fun Screen() {
        val state = viewModel.state
        val billers = listOf(Biller("Zain","ZAIN"),Biller("Batelco","BAT"),Biller("VIVA","STC"))

        Column {
            InputField(
                value = state.email,
                onValueChanged = { text -> viewModel.onEvent(RegistrationFormEvent.EmailChanged(text)) },
                hint = "Beneficiary Name",
                modifier = Modifier.fillMaxWidth()
            )
            InputField(
                value = state.password,
                onValueChanged = { text ->
                    viewModel.onEvent(
                        RegistrationFormEvent.PasswordChanged(
                            text
                        )
                    )
                },
                hint = "Beneficiary Institution",
                modifier = Modifier.fillMaxWidth()
            )
            Spinner(billers)
            if (state.services != null)
            {
                ServicesSpinner(state.services)
            }

        }
    }

}

