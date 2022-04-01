#!/bin/sh
find -type f -name *.class -delete
javac -cp "$(pwd)/src:$(pwd)/res" src/friendless/games/filler/Filler.java
javac -cp "$(pwd)/src:$(pwd)/res" src/friendless/games/filler/player/*.java
