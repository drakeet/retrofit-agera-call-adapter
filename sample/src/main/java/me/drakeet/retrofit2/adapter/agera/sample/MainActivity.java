package me.drakeet.retrofit2.adapter.agera.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.agera.Function;
import com.google.android.agera.Functions;
import com.google.android.agera.Repository;
import com.google.android.agera.Reservoir;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import me.drakeet.retrofit2.adapter.agera.AgeraCallAdapterFactory;
import me.drakeet.retrofit2.adapter.agera.Ageras;
import me.drakeet.retrofit2.adapter.agera.HttpException;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity implements Updatable {

  private Repository<String[]> repository1, repository2;
  private TextView textView;
  private static final String[] INITIAL_VALUE = {};

  interface Service {
    @GET("1") Reservoir<Gank> android();

    @GET("{page}") Reservoir<Response<Gank>> android(@Path("page") int page);
  }

  private Function<Gank, String[]> gankToTitleArray = Functions.functionFrom(Gank.class)
      .apply(gank -> gank.results)
      .apply(Functions.functionFromListOf(Gank.ResultsEntity.class)
          .thenMap(entity -> entity.desc))
      .thenApply(list -> list.toArray(new String[list.size()]));


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

    repository1 = Ageras.goToBackgroundWithInitialValue(INITIAL_VALUE)
        .attemptGetFrom(service.android())
        .orSkip()
        .thenTransform(gankToTitleArray)
        .compile();

    repository2 = Ageras.goToBackgroundWithInitialValue(INITIAL_VALUE)
        .attemptGetFrom(service.android(2))
        .orSkip()
        .attemptTransform(response -> {
          Result<Gank> result;
          if (response.isSuccessful()) {
            result = Result.success(response.body());
          } else {
            result = Result.failure(new HttpException(response));
          }
          return result;
        })
        .orEnd(input -> new String[] { "..." })
        .thenTransform(gankToTitleArray)
        .compile();

    repository1.addUpdatable(this);
    repository2.addUpdatable(new Updatable() {
      @Override public void update() {
        for (String s : repository2.get()) {
          textView.append("* " + s + "\n");
        }
        repository2.removeUpdatable(this);
      }
    });
  }


  @Override public void update() {
    for (String s : repository1.get()) {
      textView.append("* " + s + "\n");
    }
  }


  @Override protected void onDestroy() {
    super.onDestroy();
    repository1.removeUpdatable(this);
  }
}
