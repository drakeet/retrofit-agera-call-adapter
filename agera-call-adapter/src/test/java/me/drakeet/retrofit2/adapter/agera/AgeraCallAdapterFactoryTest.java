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

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import static junit.framework.Assert.fail;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

/**
 * Created by drakeet on 16/6/3.
 */
public class AgeraCallAdapterFactoryTest {

    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    private final CallAdapter.Factory factory = AgeraCallAdapterFactory.create();
    private Retrofit retrofit;


    @Before public void setUp() {
        retrofit = new Retrofit.Builder()
            .baseUrl("http://localhost:1")
            .addConverterFactory(new StringConverterFactory())
            .addCallAdapterFactory(factory)
            .build();
    }


    @Test public void nonAgeraTypeReturnsNull() {
        CallAdapter<?, ?> adapter = factory.get(String.class, NO_ANNOTATIONS, retrofit);
        assertThat(adapter, nullValue());
    }


    @Test public void responseTypes() {
        Type oBodyClass = new TypeToken<Supplier<Result<String>>>() {}.getType();
        assertThat(factory.get(oBodyClass, NO_ANNOTATIONS, retrofit).responseType(),
            equalTo(new TypeToken<String>() {}.getType()));

        Type oBodyWildcard = new TypeToken<Supplier<Result<? extends String>>>() {}.getType();
        assertThat(factory.get(oBodyWildcard, NO_ANNOTATIONS, retrofit).responseType(),
            equalTo(new TypeToken<String>() {}.getType()));

        Type oBodyGeneric = new TypeToken<Supplier<Result<List<String>>>>() {}.getType();
        assertThat(factory.get(oBodyGeneric, NO_ANNOTATIONS, retrofit).responseType(),
            equalTo(new TypeToken<List<String>>() {}.getType()));

        Type oResponseClass = new TypeToken<Supplier<Result<Response<String>>>>() {}.getType();
        assertThat(factory.get(oResponseClass, NO_ANNOTATIONS, retrofit).responseType(),
            equalTo(new TypeToken<String>() {}.getType()));

        Type oResponseWildcard
            = new TypeToken<Supplier<Result<Response<? extends String>>>>() {}.getType();
        assertThat(factory.get(oResponseWildcard, NO_ANNOTATIONS, retrofit).responseType(),
            equalTo(new TypeToken<String>() {}.getType()));
    }


    @Test public void rawBodyTypeThrows() {
        Type reservoirType = new TypeToken<Supplier>() {}.getType();
        try {
            factory.get(reservoirType, NO_ANNOTATIONS, retrofit);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e, hasMessage(containsString(
                "Supplier return type must be parameterized as Supplier<Result<Foo>> or Supplier<Result<? extends Foo>>")));
        }
    }


    @Test public void noUseResultAsFirstInnerTypeThrows() {
        Type reservoirType = new TypeToken<Supplier<String>>() {}.getType();
        try {
            factory.get(reservoirType, NO_ANNOTATIONS, retrofit);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e, hasMessage(containsString(
                "Supplier return type must be parameterized as Supplier<Result<Foo>> or Supplier<Result<? extends Foo>>")));
        }
    }


    @Test public void rawResponseTypeThrows() {
        Type reservoirType = new TypeToken<Supplier<Result<Response>>>() {}.getType();
        try {
            factory.get(reservoirType, NO_ANNOTATIONS, retrofit);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e, hasMessage(containsString(
                "Response must be parameterized as Response<Foo> or Response<? extends Foo>")));
        }
    }
}