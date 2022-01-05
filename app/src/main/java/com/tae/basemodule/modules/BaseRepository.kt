package com.base.taelib.modules

import com.base.taelib.repository.BaseRepository
import org.koin.dsl.module

object BaseRepositoryModule {
    val module = module {
        single { BaseRepository() }
    }
}
