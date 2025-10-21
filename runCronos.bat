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

:MENU
cls
echo ===========================
echo       CRONOS - EXECUCAO
echo ===========================
echo.
echo Escolha o modo de execucao:
echo   [1] - Terminal (CLI)
echo   [2] - API
echo   [3] - Hibrido (Terminal + API)
echo   [0] - Sair
echo.
set /p MODO="Digite o numero correspondente: "

IF "%MODO%"=="1" (
    echo Iniciando CRONOS em modo Terminal...
    java --enable-native-access=ALL-UNNAMED -jar "%JAR_FILE%"
    goto FIM
)
IF "%MODO%"=="2" (
    echo Iniciando CRONOS em modo API...
    java --enable-native-access=ALL-UNNAMED -jar "%JAR_FILE%" api
    goto FIM
)
IF "%MODO%"=="3" (
    echo Iniciando CRONOS em modo Hibrido (Terminal + API)...
    java --enable-native-access=ALL-UNNAMED -jar "%JAR_FILE%" hibrido
    goto FIM
)
IF "%MODO%"=="0" (
    echo Saindo...
    exit /b 0
)

echo Opcao invalida! Tente novamente.
pause
goto MENU

:FIM
echo.
echo Programa finalizado.
pause
