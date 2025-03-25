package com.chandana.quotecrate.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chandana.quotecrate.R
import com.chandana.quotecrate.databinding.ActivitySignUpBinding
import com.chandana.quotecrate.ui.base.UiState
import com.chandana.quotecrate.ui.login.LoginActivity
import com.chandana.quotecrate.utils.extensions.displayMessage
import com.chandana.quotecrate.utils.extensions.setPasswordVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var viewModel: SignupViewModel
    private lateinit var binding: ActivitySignUpBinding
    private var passwordVisibility = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]
        addTextWatchers()
        binding.LoginTV.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.submitButton.setOnClickListener {
            createUserWithEmailAndPassword()
        }
        binding.eyeIconIV.setOnClickListener {
            passwordVisibility =
                binding.eyeIconIV.setPasswordVisibility(binding.passwordET, passwordVisibility)
        }
    }

    private fun addTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateInputs()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.emailET.addTextChangedListener(textWatcher)
        binding.passwordET.addTextChangedListener(textWatcher)
    }

    private fun validateInputs() {
        val email = binding.emailET.text.toString().trim()
        val password = binding.passwordET.text.toString()
        binding.submitButton.isEnabled = isValidEmail(email) && isValidPassword(password)
    }

    // Function to validate email
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Function to validate password
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun createUserWithEmailAndPassword() {
        lifecycleScope.launch {
            viewModel.createUserWithEmailAndPassword(
                binding.emailET.text.toString().trim(),
                binding.passwordET.text.toString().trim()
            )
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        UiState.Loading -> {
                        }

                        is UiState.Error -> {
                            displayMessage(it.message)
                        }

                        is UiState.Success -> {
                            displayMessage(
                                getString(
                                    R.string.email_verification_message,
                                    it.data.email
                                )
                            )
                            navigateToLoginScreen()
                        }
                    }
                }

            }
        }
    }

    private fun navigateToLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}