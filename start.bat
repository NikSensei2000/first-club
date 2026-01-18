@echo off
echo ==========================================
echo FirstClub Membership Service
echo ==========================================
echo.

where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Docker is not installed
    echo Please install Docker from https://www.docker.com/
    exit /b 1
)

where docker-compose >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Docker Compose is not installed
    echo Please install Docker Compose
    exit /b 1
)

echo Starting FirstClub Membership Service...
echo.

docker-compose up --build

echo.
echo Application started successfully!
echo.
echo Access the application at:
echo   - API: http://localhost:8080
echo   - Swagger UI: http://localhost:8080/swagger-ui.html
echo   - H2 Console: http://localhost:8080/h2-console
echo.
echo Press Ctrl+C to stop the application
