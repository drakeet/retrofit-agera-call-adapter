package me.drakeet.retrofit2.adapter.agera.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
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

  private Repository<Gank> repository;
  private TextView textView;
  private Gank unimportantValue = new Gank();

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
    repository = Ageras.goToBackgroundWithInitialValue(unimportantValue)
        .attemptGetFrom(service.android())
        .orSkip()
        .thenTransform(input -> input)
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
