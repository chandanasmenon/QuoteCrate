package com.chandana.quotecrate.ui.login

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandana.quotecrate.data.model.User
import com.chandana.quotecrate.ui.base.UiState
import com.chandana.quotecrate.utils.DispatcherProvider
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _uiStateGoogleSignIn = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiStateGoogleSignIn: StateFlow<UiState<User>> = _uiStateGoogleSignIn
    private val _uiStateSignIn = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiStateSignIn: StateFlow<UiState<User>> = _uiStateSignIn

    fun handleCredentialResponse(credential: Credential) {
        viewModelScope.launch(dispatcherProvider.io) {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                if (idToken.isNotEmpty()) {
                    val googleAuthCredential = GoogleAuthProvider.getCredential(idToken, null)
                    _uiStateGoogleSignIn.value = UiState.Loading
                    auth.signInWithCredential(googleAuthCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null && user.isEmailVerified) {
                                    _uiStateGoogleSignIn.value =
                                        UiState.Success(User(user.email.toString()))
                                } else {
                                    _uiStateGoogleSignIn.value =
                                        UiState.Error("Please verify your email first.")
                                }
                            } else {
                                _uiStateGoogleSignIn.value =
                                    UiState.Error(task.exception?.message ?: "Sign-in failed.")
                            }
                        }
                } else {
                    _uiStateGoogleSignIn.value = UiState.Error("Failed to get Google ID token.")
                }
            } else {
                _uiStateGoogleSignIn.value = UiState.Error("Credential is not of type Google ID!")
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiStateSignIn.value = UiState.Loading
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            _uiStateSignIn.value = UiState.Success(User(user.email.toString()))
                        } else {
                            _uiStateSignIn.value =
                                UiState.Error("Please verify your email before logging in.")
                            auth.signOut()
                        }
                    } else {
                        _uiStateSignIn.value =
                            UiState.Error("Authentication failed: ${task.exception?.message}")
                    }
                }
        }
    }

}