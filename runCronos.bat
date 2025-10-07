@echo off
REM ===========================
REM Script para executar CRONOS
REM ===========================

REM Caminho da pasta raiz do projeto
SET ROOT_DIR=%~dp0
cd /d "%ROOT_DIR%"

REM Verificar se Java está instalado
java -version >nul 2>&1
IF ERRORLEVEL 1 (
    echo ERRO: Java nao encontrado. Instale o JDK e configure a variavel PATH.
    pause
    exit /b 1
)

REM Caminho do JAR com dependencias
SET JAR_FILE=target\CRONOS-1.0-SNAPSHOT-jar-with-dependencies.jar

REM Verificar se o JAR existe
IF NOT EXIST "%JAR_FILE%" (
    echo ERRO: JAR nao encontrado! Compile o projeto primeiro usando buildCronos.bat
    pause
    exit /b 1
)

echo.
echo ===========================
echo Executando CRONOS...
echo ===========================
java -jar "%JAR_FILE%"

echo.
echo Programa finalizado.
pause
