package me.drakeet.retrofit2.adapter.agera;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.agera.BaseObservable;
import com.google.android.agera.Reservoir;
import com.google.android.agera.Result;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

import static com.google.android.agera.Preconditions.checkNotNull;

/**
 * @author drakeet
 */
class CallReservoir<T> extends BaseObservable implements Reservoir<T> {

    private final Call<T> call;


    CallReservoir(@NonNull final Call<T> call) {
        this.call = checkNotNull(call);
    }


    @NonNull @Override public Result<T> get() {
        Looper.prepare();
        Result<T> result;
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                result = Result.success(response.body());
            } else {
                result = Result.failure(new HttpException(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = Result.failure(e);
        }
        return result;
    }


    @Override public void accept(@NonNull T value) {
        // pass
    }
}
