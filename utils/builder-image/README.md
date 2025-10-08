# Builder image

Builder image contains all necessary dependencies for offline building

#### Building using docker
```
sudo ./build.sh
```

Jar files can be found in `out` directory in the project root

Docker builds intended to be deterministic/reproducible

#### Placing dependencies
```
sudo ./place-dependencies.sh
```

Dependencies will be placed in `~/.gradle/caches`, so `./gradlew --offline assemble` could be ran from the project root
