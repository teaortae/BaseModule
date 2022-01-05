package com.base.taelib.modules

import com.base.taelib.viewmodel.BaseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object BaseViewModelModule {
    val module = module {
        viewModel { BaseViewModel() }
    }
}
