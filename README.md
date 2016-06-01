# retrofit-agera-call-adapter
retrofit agera call adapter

```java
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
    ...

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
```