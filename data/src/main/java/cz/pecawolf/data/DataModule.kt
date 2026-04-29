package cz.pecawolf.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import cz.pecawolf.data.api.PhotoApi
import cz.pecawolf.data.repository.ExampleRepositoryImpl
import cz.pecawolf.data.repository.PhotoRepositoryImpl
import cz.pecawolf.domain.repository.ExampleRepository
import cz.pecawolf.domain.repository.PhotoRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {

    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }


    single {
        Retrofit.Builder()
            .baseUrl("https://api.flickr.com/services/feeds/")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single {
        get<Retrofit>().create(PhotoApi::class.java)
    }

    singleOf(::ExampleRepositoryImpl) bind ExampleRepository::class
}
