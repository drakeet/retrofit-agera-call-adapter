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

import android.support.annotation.NonNull;
import com.google.android.agera.RepositoryCompilerStates.RFlow;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.google.android.agera.Repositories.repositoryWithInitialValue;

/**
 * @author drakeet
 */
public class Ageras {

    private static class LazyLoad {
        static final Executor executor = Executors.newSingleThreadExecutor();
    }


    private static Executor getSingleThreadExecutor() {
        return LazyLoad.executor;
    }


    public static <T> RFlow<T, T, ?> goToBackgroundWithInitialValue(@NonNull final T initialValue) {
        return repositoryWithInitialValue(initialValue)
            .observe()
            .onUpdatesPerLoop()
            .goTo(getSingleThreadExecutor());
    }
}
