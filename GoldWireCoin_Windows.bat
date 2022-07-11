cd target\
ren GoldWireCoin-0.1-jar-with-dependencies.jar GWC.jar
move /Y GWC.jar ..\env\
cd ..\env\
java -jar GWC.jar
pause