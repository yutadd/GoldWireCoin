#!/bin/sh
cd ./target/
mv -f GoldWireCoin-0.1-jar-with-dependencies.jar ../env/GWC.jar
cd ../env/
java -jar GWC.jar