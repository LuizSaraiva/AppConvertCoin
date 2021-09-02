package com.example.coinconverter.data.domain.di

import com.example.coinconverter.data.domain.GetExchangeValueUseCase
import com.example.coinconverter.data.domain.ListExchangeUseCase
import com.example.coinconverter.data.domain.SaveExchangeUseCase
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object DomainModule {

    fun load() {
        loadKoinModules(userCaseModules())
    }

    private fun userCaseModules(): Module {
        return module {
            factory { GetExchangeValueUseCase(get()) }
            factory { SaveExchangeUseCase(get()) }
            factory { ListExchangeUseCase(get()) }
        }
    }
}