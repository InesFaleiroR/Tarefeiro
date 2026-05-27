@echo off
echo ============================================================
echo   O TAREFEIRO - Setup Inicial (Windows)
echo ============================================================
echo.
echo [1/3] A verificar Java 21...
java -version 2>&1 | findstr "21" >nul
if %errorlevel% neq 0 (
    echo ERRO: Java 21 nao encontrado. Instala em https://adoptium.net
    pause
    exit /b 1
)
echo     Java OK

echo.
echo [2/3] A descarregar todas as dependencias Maven...
echo     (Pode demorar 2-5 minutos na primeira vez - precisa de internet)
call mvnw.cmd dependency:go-offline -q
if %errorlevel% neq 0 (
    echo ERRO: Falhou o download das dependencias.
    echo Verifica a tua ligacao a internet e volta a tentar.
    pause
    exit /b 1
)
echo     Dependencias OK

echo.
echo [3/3] A compilar o projeto...
call mvnw.cmd clean compile -DskipTests -q
if %errorlevel% neq 0 (
    echo ERRO: Compilacao falhou. Ve os erros acima.
    pause
    exit /b 1
)
echo     Compilacao OK

echo.
echo ============================================================
echo   SUCESSO! Para correr a app:
echo     mvnw.cmd spring-boot:run
echo   Depois abre:  http://localhost:8080
echo   Login:        admin@tarefeiro.pt / admin123
echo ============================================================
pause
