# retrofit-agera-call-adapter
retrofit agera call adapter

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/drakeet/retrofit-agera-call-adapter/blob/master/LICENSE)
![maven-central](https://img.shields.io/maven-central/v/me.drakeet.mailotto/mailotto.svg) 

version name: 2.0.2, version code: 1

#### Sample

<img src="https://cloud.githubusercontent.com/assets/5214214/15715564/8988f87a-2851-11e6-920c-b4f5abcb6b80.png" width=439 height=380/>

#### Usage

To add a dependency using Gradle:

```groovy
compile 'me.drakeet.retrofit2:adapter-agera:2.0.2'

compile 'com.squareup.retrofit2:retrofit:2.0.2'
compile 'com.google.android.agera:agera:1.1.0-beta1'
```

It supports `Reservoir<T>` and `Reservoir<Response<T>>`,  
And with retrofit2, you could write your service interface like this:

```java
interface Service {
    @GET("1") Reservoir<Gank> android();
    @GET("{page}") Reservoir<Response<Gank>> android(@Path("page") int page);
}
```

And config your retrofit with agera call adapter, like this:  

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("http://drakeet.me/")
    .client(new OkHttpClient())
    .addCallAdapterFactory(AgeraCallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build();
final Service service = retrofit.create(Service.class);
```

#### Addition

And in order to avoid writing some duplicate code every time, I write a class:

```java
public class Ageras {

  public static <T> RepositoryCompilerStates.RFlow<T, T, ?> goToBackgroundWithInitialValue(
      @NonNull final T initialValue) {
    return repositoryWithInitialValue(initialValue)
        .observe()
        .onUpdatesPerLoop()
        .goTo(Executors.newSingleThreadExecutor());
  }
}
```

License
=======

    Copyright (C) 2016 drakeet.
       http://drakeet.me
       
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    

 [1]: https://github.com/drakeet/retrofit-agera-call-adapter