@echo off
echo Stopping MegaMart Microservices...
echo.

echo Stopping Spring Boot servers by port...
for %%p in (9090 9091 9092 9093 9095 9096 9097 9098) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%%p') do (
        taskkill /f /pid %%a 2>nul
        if !errorlevel!==0 echo Stopped server on port %%p
    )
)

echo.
echo Closing server windows...
taskkill /f /fi "WindowTitle eq Discovery-Server*" 2>nul
taskkill /f /fi "WindowTitle eq Config-Server*" 2>nul
taskkill /f /fi "WindowTitle eq Gateway-Server*" 2>nul
taskkill /f /fi "WindowTitle eq Auth-Server*" 2>nul
taskkill /f /fi "WindowTitle eq User-Admin-Server*" 2>nul
taskkill /f /fi "WindowTitle eq Product-Server*" 2>nul
taskkill /f /fi "WindowTitle eq Cart-Server*" 2>nul
taskkill /f /fi "WindowTitle eq Order-Payment-Server*" 2>nul

echo.
echo All MegaMart servers stopped!
echo Press any key to exit...
pause >nul