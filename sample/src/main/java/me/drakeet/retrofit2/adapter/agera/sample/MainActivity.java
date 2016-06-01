package me.drakeet.retrofit2.adapter.agera.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Reservoir;
import com.google.android.agera.Updatable;
import java.util.concurrent.Executors;
import me.drakeet.retrofit2.adapter.agera.AgeraCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity implements Updatable {

    private Repository<String> repository;
    private TextView textView;
    private String unimportantValue = "";

    interface Service {
        @GET("1") Reservoir<Gank> android();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/api/data/Android/10/")
                .client(new OkHttpClient())
                .addCallAdapterFactory(AgeraCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final Service service = retrofit.create(Service.class);
        repository = Repositories.repositoryWithInitialValue(unimportantValue)
                .observe()
                .onUpdatesPerLoop()
                .goTo(Executors.newSingleThreadExecutor())
                .attemptGetFrom(service.android())
                .orSkip()
                .transform(input -> input.results.get(0))
                .thenTransform(input -> input.desc)
                .compile();

        repository.addUpdatable(this);
    }


    @Override public void update() {
        textView.setText(repository.get().toString());
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        repository.removeUpdatable(this);
    }
}
