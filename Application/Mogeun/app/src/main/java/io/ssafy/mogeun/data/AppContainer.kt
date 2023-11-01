package io.ssafy.mogeun.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.ssafy.mogeun.network.MogeunApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val userDataRepository: SignInRepository
}

class DefaultAppContainer: AppContainer {
    private val baseUrl = "https://k9c104.p.ssafy.io:8080/API/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: MogeunApiService by lazy {
        retrofit.create(MogeunApiService::class.java)
    }

    override val userDataRepository: SignInRepository by lazy {
        NetworkSignInRepository(retrofitService)
    }
}