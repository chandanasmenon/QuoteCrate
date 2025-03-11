package com.chandana.quotecrate.di.component

import com.chandana.quotecrate.di.ActivityScope
import com.chandana.quotecrate.di.module.ActivityModule
import dagger.Component

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent