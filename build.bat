@echo off
setlocal
rem ============================================================
rem  Gera a pasta com o programa pronto para levar ao PC da loja.
rem  Resultado: BasicOrderEntrySystem\dist\SistemaComandas\SistemaComandas.exe
rem  O Java vai embutido: o PC da loja nao precisa instalar nada.
rem ============================================================

rem Pasta do JDK que tem javac, jar e jpackage. Ajuste se a versao mudar.
set "JDK=C:\Users\willy\.jdks\openjdk-25.0.2"
set "SQLITE=sqlite-jdbc-3.41.2.2.jar"

if not exist "%JDK%\bin\jpackage.exe" (
    echo [ERRO] jpackage nao encontrado em "%JDK%\bin".
    echo Ajuste a variavel JDK no topo deste arquivo.
    pause
    exit /b 1
)

cd /d "%~dp0BasicOrderEntrySystem"
if errorlevel 1 (
    echo [ERRO] Pasta BasicOrderEntrySystem nao encontrada ao lado deste arquivo.
    pause
    exit /b 1
)

if not exist "lib\%SQLITE%" (
    echo [ERRO] Nao achei lib\%SQLITE%.
    echo Baixe o driver do SQLite e coloque na pasta lib, como diz o README.
    pause
    exit /b 1
)

echo [1/4] Limpando builds antigos...
if exist build rmdir /s /q build
if exist dist rmdir /s /q dist
mkdir build\classes
mkdir build\input\lib

echo [2/4] Compilando...
"%JDK%\bin\javac" -encoding UTF-8 -cp "lib\%SQLITE%" -d build\classes -sourcepath src src\Main.java
if errorlevel 1 goto :erro

echo [3/4] Criando o JAR...
> build\manifest.txt echo Main-Class: Main
>> build\manifest.txt echo Class-Path: lib/%SQLITE%
copy /y "lib\%SQLITE%" "build\input\lib\" >nul
"%JDK%\bin\jar" cfm build\input\SistemaComandas.jar build\manifest.txt -C build\classes .
if errorlevel 1 goto :erro

echo [4/4] Empacotando com o jpackage...
"%JDK%\bin\jpackage" --type app-image --name SistemaComandas --input build\input --main-jar SistemaComandas.jar --main-class Main --dest dist --java-options "--enable-native-access=ALL-UNNAMED" --add-modules java.desktop,java.sql,java.logging,java.naming,java.management,jdk.unsupported
if errorlevel 1 goto :erro

echo.
echo Pronto! Leve a pasta inteira para o PC da loja:
echo %CD%\dist\SistemaComandas
echo Para abrir, de duplo clique em SistemaComandas.exe dentro dela.
echo.
pause
exit /b 0

:erro
echo.
echo [ERRO] O build falhou. Leia a mensagem acima.
pause
exit /b 1
