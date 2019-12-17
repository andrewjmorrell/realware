package com.pivot.pivot360.content

import android.os.Build
import com.apollographql.apollo.ApolloClient
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.security.cert.CertificateException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object GraphQLHandler {
    private var mApolloClient: ApolloClient? = null

    var apiBaseUrl = ""
    // final OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
    // final OkHttpClient httpClient=SelfSigningClientBuilder.createClient()
    val apolloClient: ApolloClient
        get() {
            if (mApolloClient == null) {
                val logging = HttpLoggingInterceptor()
                logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
                val httpClient: OkHttpClient
                httpClient = if (Build.VERSION.SDK_INT <= 23) {
                    unsafeOkHttpClient.build()
                } else {
                    OkHttpClient.Builder().addInterceptor(logging).build()
                }


                val mServerUrl = apiBaseUrl//"https://api.pivotplatform.com/graphql"
                mApolloClient = ApolloClient.builder().serverUrl(mServerUrl).callFactory(object : Call.Factory {
                    override fun newCall(request: Request): Call {
                        return httpClient.newCall(request)
                    }
                }).build()
                return mApolloClient as ApolloClient
            } else {
                return mApolloClient as ApolloClient
            }
        }

    // Create a trust manager that does not validate certificate chains
    // Install the all-trusting trust manager
    // Create an ssl socket factory with our all-trusting manager
    private val unsafeOkHttpClient: OkHttpClient.Builder
        get() {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                })
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                val sslSocketFactory = sslContext.socketFactory


                val logging = HttpLoggingInterceptor()
                logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.addInterceptor(logging)


                builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
                return builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

    enum class ApiTags {
        LOGIN,
        GET_ASSETS


    }
}
