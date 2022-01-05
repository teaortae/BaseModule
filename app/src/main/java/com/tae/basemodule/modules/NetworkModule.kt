package com.base.taelib.modules

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object NetworkModule {
    private const val RESTFUL_TIMEOUT_MI_SEC = 60 * 1000
    val module = module {
        single { getOkHttpClient() }
        single { getRetrofit(get()) }
    }

    inline fun <reified T> getService(retrofit: Retrofit): T = retrofit.create(T::class.java)

    @Throws(Exception::class)
    private fun getOkHttpClient(): OkHttpClient {

        val httpClient = OkHttpClient.Builder()

        httpClient.connectTimeout(RESTFUL_TIMEOUT_MI_SEC.toLong(), TimeUnit.MILLISECONDS)
        httpClient.readTimeout(RESTFUL_TIMEOUT_MI_SEC.toLong(), TimeUnit.MILLISECONDS)
        httpClient.writeTimeout(RESTFUL_TIMEOUT_MI_SEC.toLong(), TimeUnit.MILLISECONDS)
        httpClient.retryOnConnectionFailure(false)

        val interceptor = HttpLoggingInterceptor { message -> Log.d("network module : ", message) }

        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(interceptor)
        httpClient.addNetworkInterceptor(StethoInterceptor())
        httpClient.addInterceptor(HeaderSettingInterceptor())

        return httpClient.build()
    }

    @Throws(Exception::class)
    private fun getRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create(
                    GsonBuilder().setLenient()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .registerTypeAdapterFactory(CustomFieldTypeAdapterFactory())
                            .create())
            )
            .client(okHttpClient)
            .build()


    private class CustomFieldTypeAdapterFactory : TypeAdapterFactory {
        override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
            val adapter = gson.getDelegateAdapter(this, type)

            return object : TypeAdapter<T>() {
                @Throws(IOException::class)
                override fun write(out: JsonWriter, value: T) {
                    adapter.write(out, value)
                }

                @Throws(IOException::class)
                override fun read(js: JsonReader): T {
                    return adapter.read(js)
                }
            }
        }
    }
}

open class HeaderSettingInterceptor : Interceptor {

    companion object {
        const val CONTENT_TYPE = "Content-Type"
        const val AUTH = "Authorization"
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        return chain.runCatching {
            val headers: Headers = getHeaders(request)
            request = request
                    .newBuilder()
                    .headers(headers)
                     .build()
            proceed(request)
        }.getOrNull() ?: chain.proceed(request)
    }

    @Throws(Exception::class)
    private fun getHeaders(request: Request): Headers {
        val req = request
                .headers
                .newBuilder()
                .add(CONTENT_TYPE, "application/json")
        return req.build()
    }
}