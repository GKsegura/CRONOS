@echo off

SET ROOT_DIR=%~dp0
cd /d "%ROOT_DIR%"

echo ====================================================================================================
echo Compilando CRONOS via Maven...
echo ====================================================================================================

where java >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo ERRO: Java nao encontrado. Instale o JDK e configure o PATH.
    goto end
)

where mvn >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo ERRO: Maven nao encontrado. Instale o Maven e configure o PATH.
    goto end
)

call mvn -B clean package
IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERRO: Compilacao falhou.
    goto end
)

echo.
echo ====================================================================================================
echo Compilacao concluida com sucesso!
echo Arquivos gerados em %ROOT_DIR%target\
echo ====================================================================================================

:end
echo.
pause