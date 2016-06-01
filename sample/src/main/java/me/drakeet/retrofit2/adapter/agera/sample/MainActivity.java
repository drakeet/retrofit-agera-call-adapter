package me.drakeet.retrofit2.adapter.agera.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.agera.Functions;
import com.google.android.agera.Repository;
import com.google.android.agera.Reservoir;
import com.google.android.agera.Updatable;
import me.drakeet.retrofit2.adapter.agera.AgeraCallAdapterFactory;
import me.drakeet.retrofit2.adapter.agera.Ageras;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity implements Updatable {

  private Repository<String[]> repository;
  private TextView textView;
  private static final String[] INITIAL_VALUE = {};

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

    repository = Ageras.goToBackgroundWithInitialValue(INITIAL_VALUE)
        .attemptGetFrom(service.android())
        .orSkip()
        .transform(gank -> gank.results)
        .transform(Functions.functionFromListOf(Gank.ResultsEntity.class)
            .thenMap(entity -> entity.desc))
        .thenTransform(list -> list.toArray(new String[list.size()]))
        .compile();

    repository.addUpdatable(this);
  }


  @Override public void update() {
    for (String s : repository.get()) {
      textView.append("* " + s + "\n");
    }
  }


  @Override protected void onDestroy() {
    super.onDestroy();
    repository.removeUpdatable(this);
  }
}
