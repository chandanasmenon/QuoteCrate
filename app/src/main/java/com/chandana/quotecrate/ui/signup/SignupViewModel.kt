package com.chandana.quotecrate.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandana.quotecrate.data.model.User
import com.chandana.quotecrate.ui.base.UiState
import com.chandana.quotecrate.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState

    fun createUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.value = UiState.Loading
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            user.sendEmailVerification()
                                .addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        _uiState.value = UiState.Success(
                                            User(user.email.toString())
                                        )
                                    } else {
                                        _uiState.value = UiState.Error(
                                            "Failed to send verification email: ${verificationTask.exception?.message}"
                                        )
                                    }
                                }
                        } else {
                            _uiState.value = UiState.Error("User creation failed.")
                        }
                    } else {
                        _uiState.value = UiState.Error(
                            message = (task.exception?.message
                                ?: "Authentication failed.").toString()
                        )
                    }
                }
        }
    }

}