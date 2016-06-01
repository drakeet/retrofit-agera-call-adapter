# retrofit-agera-call-adapter
retrofit agera call adapter

```java
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
        ...
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
```