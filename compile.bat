@echo off
echo ========================================================
echo   Compiling Galaxy Defender: DSA Edition (KDU IT22013)
echo ========================================================
if not exist bin mkdir bin
javac -d bin src\datastructures\*.java src\model\*.java src\algorithms\*.java src\ui\*.java src\Main.java
if %errorlevel% equ 0 (
    echo [SUCCESS] Build succeeded with 0 errors!
    if exist "C:\Program Files\Java\jdk-18\bin\jar.exe" (
        "C:\Program Files\Java\jdk-18\bin\jar.exe" cfe GalaxyDefender.jar Main -C bin . >nul 2>&1
        echo [PACKAGE] GalaxyDefender.jar updated successfully.
    )
) else (
    echo [ERROR] Compilation failed.
)
pause
