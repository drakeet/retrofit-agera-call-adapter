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

import com.google.android.agera.FailedResultException;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import static okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AFTER_REQUEST;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

/**
 * @author drakeet
 */
public class SupplierTest {

    @Rule public final MockWebServer server = new MockWebServer();
    private Service service;


    interface Service {
        @GET("/") Supplier<Result<String>> body();
        @GET("/") Supplier<Result<Response<String>>> response();
    }


    @Before public void setUp() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(new StringConverterFactory())
            .addCallAdapterFactory(AgeraCallAdapterFactory.create())
            .build();
        service = retrofit.create(Service.class);
    }


    @Test public void bodySuccess200() {
        server.enqueue(new MockResponse().setBody("Hi"));

        Result<String> result = service.body().get();
        assertThat(result.get(), equalTo("Hi"));
    }


    @Test public void bodySuccess404() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));

        Result<String> result = service.body().get();
        try {
            result.get();
            fail();
        } catch (FailedResultException e) {
            assertThat(e.getCause(), instanceOf(HttpException.class));
            assertThat(e.getCause(), hasMessage(containsString("HTTP 404 Client Error")));
        }
    }


    @Test public void bodyFailure() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

        Result<String> result = service.body().get();
        try {
            result.get();
            fail();
        } catch (FailedResultException e) {
            assertThat(e.getCause(), instanceOf(IOException.class));
        }
    }


    @Test public void responseSuccess200() throws Exception {
        server.enqueue(new MockResponse().setBody("Hi"));

        Supplier<Result<Response<String>>> supplier = service.response();
        Response<String> response = supplier.get().get();
        assertTrue(response.isSuccessful());
        assertEquals(response.body(), "Hi");
    }


    // TODO: 16/6/8 Cannot get() from a failed result
    @Test public void responseSuccess404() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("error"));

        Supplier<Result<Response<String>>> supplier = service.response();
        try {
            supplier.get().get();
            fail();
        } catch (FailedResultException e) {
            assertThat(e.getCause(), instanceOf(HttpException.class));
            assertThat(e.getCause(), hasMessage(containsString("HTTP 404 Client Error")));
        }
    }


    @Test public void responseFailure() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

        Supplier<Result<Response<String>>> supplier = service.response();
        try {
            supplier.get().get();
            fail();
        } catch (FailedResultException e) {
            assertThat(e.getCause(), instanceOf(IOException.class));
        }
    }
}