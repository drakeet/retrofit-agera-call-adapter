package me.drakeet.retrofit2.adapter.agera;

import android.support.annotation.NonNull;
import com.google.android.agera.RepositoryCompilerStates.RFlow;
import java.util.concurrent.Executors;

import static com.google.android.agera.Repositories.repositoryWithInitialValue;

/**
 * @author drakeet
 */
public class Ageras {

    public static <T> RFlow<T, T, ?> goToBackgroundWithInitialValue(@NonNull final T initialValue) {
        return repositoryWithInitialValue(initialValue)
            .observe()
            .onUpdatesPerLoop()
            .goTo(Executors.newSingleThreadExecutor());
    }
}
