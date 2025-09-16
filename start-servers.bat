@echo off
echo Starting MegaMart Microservices...
echo.

echo [1/3] Starting Discovery Server (Port 9092)...
start "Discovery-Server" cmd /k "cd discovery-server && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [2/3] Starting Config Server (Port 9091)...
start "Config-Server" cmd /k "cd config-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [3/3] Starting Gateway Server (Port 9090)...
start "Gateway-Server" cmd /k "cd gateway-server && mvn spring-boot:run"

echo.
echo All servers are starting up!
echo.
echo Access your application at:
echo - Gateway Server: http://localhost:9090
echo - Discovery Server: http://localhost:9092
echo - Config Server: http://localhost:9091
echo.
echo Press any key to exit this window...
pause >nul