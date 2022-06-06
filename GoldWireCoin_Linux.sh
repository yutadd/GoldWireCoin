#!/bin/sh
cd ./target/
mv -f GoldWireCoin-0.1-jar-with-dependencies.jar ./GWC/GWC.jar
cd ./GWC/
java -jar GWC.jar