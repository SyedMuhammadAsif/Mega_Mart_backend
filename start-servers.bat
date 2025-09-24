@echo off
echo Starting MegaMart Microservices...
echo.

echo [1/8] Starting Discovery Server (Port 9092)...
start "Discovery-Server" cmd /k "cd discovery-server && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [2/8] Starting Config Server (Port 9091)...
start "Config-Server" cmd /k "cd config-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [3/8] Starting Gateway Server (Port 9090)...
start "Gateway-Server" cmd /k "cd gateway-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [4/8] Starting Auth Server (Port 9093)...
start "Auth-Server" cmd /k "cd auth-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [5/8] Starting User Admin Server (Port 9095)...
start "User-Admin-Server" cmd /k "cd user-admin-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [6/8] Starting Product Server (Port 9096)...
start "Product-Server" cmd /k "cd product-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [7/8] Starting Cart Server (Port 9097)...
start "Cart-Server" cmd /k "cd cart-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [8/8] Starting Order Payment Server (Port 9098)...
start "Order-Payment-Server" cmd /k "cd order-payment-server && mvn spring-boot:run"

echo.
echo All servers are starting up!
echo.
echo Access your application at:
echo - Gateway Server: http://localhost:9090
echo - Config Server: http://localhost:9091
echo - Discovery Server: http://localhost:9092
echo - Auth Server: http://localhost:9093
echo - User Admin Server: http://localhost:9095
echo - Product Server: http://localhost:9096
echo - Cart Server: http://localhost:9097
echo - Order Payment Server: http://localhost:9098
echo.
echo Press any key to exit this window...
pause >nul
