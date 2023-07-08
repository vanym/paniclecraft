# Building

### Clone PanicleCraft repository

```
git clone https://github.com/vanym/paniclecraft.git -b 1.14.4 paniclecraft-1.14.4
cd paniclecraft-1.14.4
```

#### Build using gradle with java 8
```
./gradlew jar
```

Jar file can be found in `build/libs` directory

#### Build using docker
```
sudo ./build-docker.sh
```

Jar file can be found in `out` directory

Docker builds intended to be deterministic/reproducible
