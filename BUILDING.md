# Building

### Clone PanicleCraft repository

```
git clone https://github.com/vanym/paniclecraft.git -b 1.7.10 paniclecraft-1.7.10
cd paniclecraft-1.7.10
```

#### Building using gradle with java 8
```
./gradlew assemble
```

Jar files can be found in `build/libs` directory

#### Building using docker
```
sudo ./build-docker.sh
```

Jar files can be found in `out` directory

Docker builds intended to be deterministic/reproducible

In case of problems with downloading dependencies checkout [builder image](utils/builder-image)
