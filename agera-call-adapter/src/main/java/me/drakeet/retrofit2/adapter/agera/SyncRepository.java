package me.drakeet.retrofit2.adapter.agera;

import android.support.annotation.NonNull;
import com.google.android.agera.BaseObservable;
import com.google.android.agera.Repository;
import com.google.android.agera.Supplier;

/**
 * Created by drakeet on 16/5/30.
 */

public class SyncRepository<T> extends BaseObservable implements Repository<T> {

    private final Supplier<T> supplier;


    public SyncRepository(Supplier<T> supplier) {this.supplier = supplier;}


    @NonNull @Override public T get() {
        return supplier.get();
    }
}
