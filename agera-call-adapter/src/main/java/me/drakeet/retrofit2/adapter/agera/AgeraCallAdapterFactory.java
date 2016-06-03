/*
 * Copyright (C) 2016 drakeet.
 *     http://drakeet.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.drakeet.retrofit2.adapter.agera;

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

    public static AgeraCallAdapterFactory create() {
        return new AgeraCallAdapterFactory();
    }


    private AgeraCallAdapterFactory() {
    }


    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Reservoir.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("Reservoir return type must be parameterized"
                + " as Reservoir<Foo> or Reservoir<? extends Foo>");
        }

        Type innerType = getParameterUpperBound(0, (ParameterizedType) returnType);
        if (getRawType(innerType) != Response.class) {
            // Generic type is not Response<T>. Use it for body-only adapter.
            return new BodyCallAdapter(innerType);
        }

        // Generic type is Response<T>. Extract T and create the Response version of the adapter.
        if (!(innerType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parameterized"
                + " as Response<Foo> or Response<? extends Foo>");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) innerType);
        return new ResponseCallAdapter(responseType);
    }


    private static final class ResponseCallAdapter implements CallAdapter<Reservoir<?>> {

        private final Type responseType;


        ResponseCallAdapter(Type responseType) {
            this.responseType = responseType;
        }


        @Override public Type responseType() {
            return responseType;
        }


        @Override public <T> Reservoir<Response<T>> adapt(Call<T> call) {
            return new CallResponseReservoir(call);
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


        @Override public <T> Reservoir<T> adapt(final Call<T> call) {
            return new CallReservoir<>(call);
        }
    }
}
