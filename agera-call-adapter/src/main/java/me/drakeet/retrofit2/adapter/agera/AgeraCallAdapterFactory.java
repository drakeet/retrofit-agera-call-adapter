package me.drakeet.retrofit2.adapter.agera;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Supplier;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by drakeet on 16/5/29.
 */
public final class AgeraCallAdapterFactory extends CallAdapter.Factory {

    private static final String TAG = AgeraCallAdapterFactory.class.getSimpleName();


    public static AgeraCallAdapterFactory create() {
        return new AgeraCallAdapterFactory();
    }


    private AgeraCallAdapterFactory() {
    }


    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        String canonicalName = rawType.getCanonicalName();
        Log.v(TAG, canonicalName);
        CallAdapter<Repository<?>> callAdapter = getCallAdapter(returnType);
        return callAdapter;
    }


    private CallAdapter<Repository<?>> getCallAdapter(Type returnType) {
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized"
                        + " as Response<Foo> or Response<? extends Foo>");
            }
            Type responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            return new ResponseCallAdapter(responseType);
        }
        return new BodyCallAdapter(observableType);
    }


    static final class ResponseCallAdapter implements CallAdapter<Repository<?>> {

        private final Type responseType;


        ResponseCallAdapter(Type responseType) {
            this.responseType = responseType;
        }


        @Override public Type responseType() {
            return responseType;
        }


        @Override public <R> Repository<Response<R>> adapt(Call<R> call) {
            Repository<Response<R>> repository = null;
            try {
                repository = Repositories.mutableRepository(call.execute());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return repository;
        }
    }

    private static class BodyCallAdapter implements CallAdapter<Repository<?>> {

        private final Type responseType;


        BodyCallAdapter(Type responseType) {
            this.responseType = responseType;
        }


        @Override public Type responseType() {
            return responseType;
        }


        @Override public <R> Repository<R> adapt(final Call<R> call) {
            Repository<R> repository;
            repository = new SyncRepository<>(new Supplier<R>() {
                @NonNull @Override public R get() {
                    Looper.prepare();
                    try {
                        return call.execute().body();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            return repository;
        }
    }
}
