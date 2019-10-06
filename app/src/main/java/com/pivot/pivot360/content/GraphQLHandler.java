package com.pivot.pivot360.content;

import android.os.Build;

import com.apollographql.apollo.ApolloClient;

import org.jetbrains.annotations.NotNull;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class GraphQLHandler {
    private static String mServerUrl = "https://api.pivotplatform.com/graphql";
    private static ApolloClient mApolloClient;


    public static ApolloClient getApolloClient() {
        if (mApolloClient == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // final OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
            // final OkHttpClient httpClient=SelfSigningClientBuilder.createClient()
            OkHttpClient httpClient;
            if (Build.VERSION.SDK_INT <= 23) {
                httpClient = getUnsafeOkHttpClient().build();
            } else {
                httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
            }

            final OkHttpClient finalHttpClient = httpClient;
            mApolloClient = ApolloClient.builder().serverUrl(mServerUrl).callFactory(new Call.Factory() {
                @Override
                public Call newCall(@NotNull Request request) {
                    return finalHttpClient.newCall(request);
                }
            }).build();
            return mApolloClient;
        } else {
            return mApolloClient;
        }
    }

    public enum ApiTags {
        LOGIN,
        GET_ASSETS


    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.addInterceptor(logging);


            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
