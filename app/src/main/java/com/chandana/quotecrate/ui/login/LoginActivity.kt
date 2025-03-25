package com.chandana.quotecrate.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chandana.quotecrate.R
import com.chandana.quotecrate.databinding.ActivityLoginBinding
import com.chandana.quotecrate.ui.base.UiState
import com.chandana.quotecrate.ui.quoteDisplay.QuoteActivity
import com.chandana.quotecrate.ui.signup.SignUpActivity
import com.chandana.quotecrate.utils.extensions.displayMessage
import com.chandana.quotecrate.utils.extensions.setPasswordVisibility
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: SignInViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var passwordVisibility = true
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                navigateToHomeScreen()
            } else {
                auth.signOut()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        addTextWatchers()
        binding.signInButton.setOnClickListener {
            signInWithEmailAndPassword()
        }
        binding.signInGoogleButton.setOnClickListener {
            signInWithGoogle()
        }
        binding.signupTV.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
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
        val password = binding.passwordET.text.toString().trim()
        binding.signInButton.isEnabled =
            email.isNotEmpty() && isValidEmail(email) && password.isNotEmpty()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signInWithGoogle() {
        val signInOption =
            GetSignInWithGoogleOption.Builder(getString(R.string.default_web_client_id))
                .setNonce("")
                .build()
        val request = buildCredentialRequest(signInOption)

        lifecycleScope.launch {
            try {
                handleCredentialResponse(request)
            } catch (e: GetCredentialCancellationException) {
                displayMessage(getString(R.string.sign_in_was_canceled_text))
            } catch (e: Exception) {
                displayMessage(e.message ?: getString(R.string.an_error_occurred_during_sign_in))
            }
        }
    }

    private fun buildCredentialRequest(option: CredentialOption): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }

    private suspend fun handleCredentialResponse(request: GetCredentialRequest) {
        try {
            val credential = CredentialManager.create(this@LoginActivity)
                .getCredential(this@LoginActivity, request)
                .credential
            viewModel.handleCredentialResponse(credential)

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateGoogleSignIn.collectLatest {
                    when (it) {
                        UiState.Loading -> {
                        }

                        is UiState.Error -> {
                            displayMessage(it.message)
                        }

                        is UiState.Success -> {
                            displayMessage(getString(R.string.user_authenticated_successfully))
                            val sharedPref = getSharedPreferences(
                                getString(R.string.userinfo_text),
                                MODE_PRIVATE
                            )
                            val editor = sharedPref.edit()
                            editor.putString(getString(R.string.email), it.data.email)
                            editor.apply()
                            navigateToHomeScreen()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            displayMessage(e.message ?: getString(R.string.something_went_wrong_text))
        }
    }

    private fun signInWithEmailAndPassword() {
        lifecycleScope.launch {
            viewModel.signInWithEmailAndPassword(
                binding.emailET.text.toString().trim(),
                binding.passwordET.text.toString().trim()
            )
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateSignIn.collectLatest {
                    when (it) {
                        UiState.Loading -> {
                        }

                        is UiState.Error -> {
                            displayMessage(it.message)
                        }

                        is UiState.Success -> {
                            displayMessage(getString(R.string.user_authenticated_successfully))
                            val sharedPref = getSharedPreferences(
                                getString(R.string.userinfo_text),
                                MODE_PRIVATE
                            )
                            val editor = sharedPref.edit()
                            editor.putString(getString(R.string.email), it.data.email)
                            editor.apply()
                            navigateToHomeScreen()
                        }
                    }

                }
            }

        }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, QuoteActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}