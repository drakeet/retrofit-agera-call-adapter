package me.drakeet.retrofit2.adapter.agera;

import android.util.Log;
import com.google.android.agera.Reservoir;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author drakeet
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
    CallAdapter<Reservoir<?>> callAdapter = getCallAdapter(returnType);
    return callAdapter;
  }


  private CallAdapter<Reservoir<?>> getCallAdapter(Type returnType) {
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


  static final class ResponseCallAdapter implements CallAdapter<Reservoir<?>> {

    private final Type responseType;


    ResponseCallAdapter(Type responseType) {
      this.responseType = responseType;
    }


    @Override public Type responseType() {
      return responseType;
    }


    @Override public <R> Reservoir<Response<R>> adapt(Call<R> call) {
      Reservoir<Response<R>> repository = null;
      // TODO: 16/6/1  
      return repository;
    }
  }

  private static class BodyCallAdapter implements CallAdapter<Reservoir<?>> {

    private final Type responseType;


    BodyCallAdapter(Type responseType) {
      this.responseType = responseType;
    }


    @Override public Type responseType() {
      return responseType;
    }


    @Override public <R> Reservoir<R> adapt(final Call<R> call) {
      Reservoir<R> reservoir;
      reservoir = new CallReservoir<>(call);
      return reservoir;
    }
  }
}
