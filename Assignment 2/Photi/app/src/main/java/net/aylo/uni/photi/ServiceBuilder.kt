package net.aylo.uni.photi

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
    Creates class used for HTTP client to connect to API and retrieve data
 */

object ServiceBuilder {
    private const val URL ="https://api.comp50011.uni.aylo.net/"
    private val okHttp = OkHttpClient.Builder()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())
        .build()

    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }

}