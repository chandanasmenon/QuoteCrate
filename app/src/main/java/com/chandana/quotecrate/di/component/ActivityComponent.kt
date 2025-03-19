package com.chandana.quotecrate.di.component

import com.chandana.quotecrate.di.ActivityScope
import com.chandana.quotecrate.di.module.ActivityModule
import com.chandana.quotecrate.ui.login.LoginActivity
import com.chandana.quotecrate.ui.signup.SignUpActivity
import dagger.Component

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: SignUpActivity)
}