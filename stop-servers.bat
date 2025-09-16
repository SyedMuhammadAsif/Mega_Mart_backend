@echo off
echo Stopping MegaMart Microservices...
echo.

echo Killing all Java processes (Spring Boot servers)...
taskkill /f /im java.exe 2>nul
if %errorlevel%==0 (
    echo All servers stopped successfully!
) else (
    echo No running Java processes found.
)

echo.
echo Closing all server windows...
taskkill /f /fi "WindowTitle eq Discovery-Server*" 2>nul
taskkill /f /fi "WindowTitle eq Config-Server*" 2>nul
taskkill /f /fi "WindowTitle eq Gateway-Server*" 2>nul

echo.
echo All servers have been stopped!
echo Press any key to exit...
pause >nul