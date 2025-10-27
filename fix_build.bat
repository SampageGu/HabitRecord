@echo off
echo ================================================
echo Gradle Build Troubleshooting Script
echo ================================================
echo.

echo Step 1: Cleaning local project caches...
cd /d D:\0softfiles\androidStudioFiles\habitRecord
if exist .gradle rd /s /q .gradle
if exist app\build rd /s /q app\build
if exist build rd /s /q build
echo ✓ Local cache cleaned

echo.
echo Step 2: Cleaning Gradle user cache (transforms)...
if exist %USERPROFILE%\.gradle\caches\transforms-3 (
    rd /s /q %USERPROFILE%\.gradle\caches\transforms-3
    echo ✓ Transforms cache cleaned
) else (
    echo - Transforms cache not found (already clean)
)

if exist %USERPROFILE%\.gradle\caches\modules-2 (
    echo Cleaning modules cache (optional, helps with dependency issues)...
    rd /s /q %USERPROFILE%\.gradle\caches\modules-2
    echo ✓ Modules cache cleaned
)

echo.
echo Step 3: Stopping Gradle daemon...
where gradlew.bat >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    call gradlew --stop 2>nul
    echo ✓ Gradle daemon stopped
) else (
    echo - gradlew.bat not found (will be generated on sync)
)

echo.
echo ================================================
echo Cache cleaning completed!
echo ================================================
echo.
echo NEXT STEP: Please open this project in Android Studio
echo.
echo 1. Open Android Studio
echo 2. Open project: D:\0softfiles\androidStudioFiles\habitRecord
echo 3. Wait for Gradle Sync to complete (5-10 minutes)
echo 4. Build > Clean Project
echo 5. Build > Rebuild Project
echo 6. Run the app
echo.
echo The gradlew.bat file will be automatically generated
echo when Android Studio syncs the project.
echo.

pause

