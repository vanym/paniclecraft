# Builder image

Builder image contains all necessary dependencies for offline building

#### Building using docker
```
sudo ./build.sh
```

Jar file can be found in `out` directory in the project root

Docker builds intended to be deterministic/reproducible

#### Placing dependencies
```
sudo ./place-dependencies.sh
```

Dependencies will be placed in `~/.gradle/caches`, so `./gradlew --offline jar` could be ran from the project root
