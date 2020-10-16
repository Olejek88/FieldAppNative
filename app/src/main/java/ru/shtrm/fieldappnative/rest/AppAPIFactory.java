package ru.shtrm.fieldappnative.rest;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.shtrm.fieldappnative.AuthorizedUser;
import ru.shtrm.fieldappnative.BuildConfig;
import ru.shtrm.fieldappnative.FieldApplication;
import ru.shtrm.fieldappnative.deserializer.DateTypeDeserializer;
import ru.shtrm.fieldappnative.rest.interfaces.IChannel;
import ru.shtrm.fieldappnative.rest.interfaces.IMeasureType;
import ru.shtrm.fieldappnative.rest.interfaces.IMeasuredValue;
import ru.shtrm.fieldappnative.rest.interfaces.ITokenService;
import ru.shtrm.fieldappnative.rest.interfaces.IUserService;

public class
AppAPIFactory {
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;
    private static final OkHttpClient CLIENT = new OkHttpClient()
            .newBuilder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request origRequest = chain.request();
                    Headers origHeaders = origRequest.headers();
                    AuthorizedUser user = AuthorizedUser.getInstance();
                    Headers newHeaders = origHeaders.newBuilder()
                            .add("Authorization", user.getBearer()).build();

                    Request.Builder requestBuilder = origRequest.newBuilder().headers(newHeaders);
                    String login = user.getLogin();
                    if (login != null) {
                        HttpUrl url = origRequest.url().newBuilder()
                                .addQueryParameter("apiuser", login).build();
                        requestBuilder.url(url);
                    }

                    Request newRequest = requestBuilder.build();
                    return chain.proceed(newRequest);
                }
            })
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    if (BuildConfig.DEBUG) {
                        Request request = chain.request();
                        HttpUrl url = request.url()
                                .newBuilder()
                                .addQueryParameter("XDEBUG_SESSION_START", "xdebug")
                                .build();
                        Request.Builder requestBuilder = request.newBuilder().url(url);
                        Request newRequest = requestBuilder.build();
                        return chain.proceed(newRequest);
                    } else {
                        return chain.proceed(chain.request());
                    }
                }
            })
            //.sslSocketFactory(sslsf, tm)
            .build();

    @NonNull
    public static ITokenService getTokenService() {
        return getRetrofit().create(ITokenService.class);
    }

    @NonNull
    public static IUserService getUserService() {
        return getRetrofit().create(IUserService.class);
    }

    @NonNull
    public static IChannel getChannelService() {
        return getRetrofit().create(IChannel.class);
    }

    @NonNull
    public static IMeasuredValue getMeasuredValueService() {
        return getRetrofit().create(IMeasuredValue.class);
    }

    @NonNull
    public static IMeasureType getMeasureTypeService() {
        return getRetrofit().create(IMeasureType.class);
    }

    /**
     * Метод без указания специфической обработки элементов json.
     *
     * @return Retrofit
     */
    @NonNull
    private static Retrofit getRetrofit() {
        return getRetrofit(null);
    }

    /**
     * Метод для задания специфической обработки элементов json.
     *
     * @param list Список типов и десереализаторов.
     * @return Retrofit
     */
    @NonNull
    private static Retrofit getRetrofit(List<TypeAdapterParam> list) {
        GsonBuilder builder = new GsonBuilder();

        if (list != null) {
            for (TypeAdapterParam param : list) {
                builder.registerTypeAdapter(param.getTypeClass(), param.getDeserializer());
            }
        }

        builder.registerTypeAdapter(Date.class, new DateTypeDeserializer());
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        builder.serializeNulls();
        builder.setLenient();
        Gson gson = builder.create();
        return new Retrofit.Builder()
                .baseUrl(FieldApplication.serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(CLIENT)
                .build();
    }

//        public static final class Actions {
//        public static final String ACTION_GET_TOKEN = "action_get_token";
//        public static final String ACTION_GET_USER = "action_get_user";
//        public static final String ACTION_GET_ALL_REFERENCE = "action_get_all_reference";
//    }

    /**
     * Класс для хранения типа и десереализатора к этому типу.
     */
    private static class TypeAdapterParam {
        Class<?> typeClass;
        JsonDeserializer<?> deserializer;

        TypeAdapterParam(Class<?> c, JsonDeserializer<?> d) {
            typeClass = c;
            deserializer = d;
        }

        public Class<?> getTypeClass() {
            return typeClass;
        }

        public void setTypeClass(Class<?> typeClass) {
            this.typeClass = typeClass;
        }

        public JsonDeserializer<?> getDeserializer() {
            return deserializer;
        }

        public void setDeserializer(JsonDeserializer<?> deserializer) {
            this.deserializer = deserializer;
        }
    }

    public static class UnifiedTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public UnifiedTrustManager(KeyStore localKeyStore) throws KeyStoreException {
            try {
                this.defaultTrustManager = createTrustManager(null);
                this.localTrustManager = createTrustManager(localKeyStore);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        private X509TrustManager createTrustManager(KeyStore store) throws NoSuchAlgorithmException, KeyStoreException {
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(store);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            return (X509TrustManager) trustManagers[0];
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkClientTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkClientTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] first = defaultTrustManager.getAcceptedIssuers();
            X509Certificate[] second = localTrustManager.getAcceptedIssuers();
            X509Certificate[] result = Arrays.copyOf(first, first.length + second.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }
    }
}
