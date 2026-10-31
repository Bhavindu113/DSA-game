@echo off
echo ========================================================
echo   Launching Galaxy Defender: DSA Edition
echo   General Sir John Kotelawala Defence University (KDU)
echo   IT22013 Data Structures and Algorithms Mini-Game
echo ========================================================
if exist GalaxyDefender.jar (
    java -jar GalaxyDefender.jar
) else (
    java -cp bin Main
)
pause
