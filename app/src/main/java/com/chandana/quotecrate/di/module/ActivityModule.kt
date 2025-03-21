package com.chandana.quotecrate.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.chandana.quotecrate.data.repository.QuoteRepository
import com.chandana.quotecrate.di.ActivityContext
import com.chandana.quotecrate.ui.base.ViewModelProviderFactory
import com.chandana.quotecrate.ui.login.SignInViewModel
import com.chandana.quotecrate.ui.quoteDisplay.QuoteViewModel
import com.chandana.quotecrate.ui.signup.SignupViewModel
import com.chandana.quotecrate.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @ActivityContext
    @Provides
    fun provideContext(): Context {
        return activity
    }

    @Provides
    fun provideSignInViewModel(
        auth: FirebaseAuth,
        dispatcherProvider: DispatcherProvider
    ): SignInViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(SignInViewModel::class) {
            SignInViewModel(auth, dispatcherProvider)
        })[SignInViewModel::class.java]
    }

    @Provides
    fun provideSignUpViewModel(
        auth: FirebaseAuth,
        dispatcherProvider: DispatcherProvider
    ): SignupViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(SignupViewModel::class) {
            SignupViewModel(auth, dispatcherProvider)
        })[SignupViewModel::class.java]
    }

    @Provides
    fun provideQuoteViewModel(
        dispatcherProvider: DispatcherProvider,
        repository: QuoteRepository
    ): QuoteViewModel {
        return ViewModelProvider(activity, ViewModelProviderFactory(QuoteViewModel::class) {
            QuoteViewModel(dispatcherProvider, repository)
        })[QuoteViewModel::class.java]
    }

}