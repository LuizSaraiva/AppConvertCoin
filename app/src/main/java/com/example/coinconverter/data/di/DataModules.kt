package com.example.coinconverter.data.di

import android.util.Log
import com.example.coinconverter.data.database.AppDatabase
import com.example.coinconverter.data.repository.CoinRepository
import com.example.coinconverter.data.repository.CoinRepositoryImp
import com.example.coinconverter.data.services.AwesomeServices
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataModules {

    private const val HTTP_TAG = "OkHttp"

    fun load() {
        loadKoinModules(networkModule() + repositoryModules() + databaseModule())
    }

    private fun repositoryModules(): Module {
        return module {
            single<CoinRepository> {
                CoinRepositoryImp(get(), get())
            }
        }
    }

    private fun databaseModule(): Module {
        return module {
            single {
                AppDatabase.getInstance(androidApplication())
            }
        }
    }

    private fun networkModule(): Module {
        return module {
            single {
                val interceptor = HttpLoggingInterceptor {
                    Log.e(HTTP_TAG, ": $it")
                }
                interceptor.level = HttpLoggingInterceptor.Level.BODY

                OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()
            }

            single {
                GsonConverterFactory.create(GsonBuilder().create())
            }

            single {
                createService<AwesomeServices>(get(), get())
            }
        }
    }

    private inline fun <reified T> createService(
        client: OkHttpClient,
        factory: GsonConverterFactory
    ): T {
        return Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br")
            .client(client)
            .addConverterFactory(factory)
            .build()
            .create(T::class.java)
    }

}