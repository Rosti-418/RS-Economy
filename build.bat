@echo off
setlocal enabledelayedexpansion

echo =============================================
echo Minecraft Mod Multi-Version Builder
echo =============================================

rem --- Eingaben abfragen
set /p mc_versions=Gib Minecraft-Versionen ein (z.B. 1.20.1,1.21.1): 
set /p neo_versions=Gib passende NeoForge-Versionen ein (z.B. 20.1.73,2.0.74): 

rem --- Arrays vorbereiten
set i=0
set "mc_arr="
set "neo_arr="

for %%v in (%mc_versions%) do (
    set /a i+=1
    set mc_arr[!i!]=%%v
)
set count=%i%

set i=0
for %%v in (%neo_versions%) do (
    set /a i+=1
    set neo_arr[!i!]=%%v
)

if not %count%==%i% (
    echo Fehler: Die Anzahl der Minecraft- und NeoForge-Versionen stimmt nicht überein.
    exit /b 1
)

echo ---------------------------------------------
echo Starte Builds fuer %count% Kombination(en)
echo ---------------------------------------------

rem --- Loop über alle Paare
set j=1
:build_loop
if %j% GTR %count% goto done

set mc=!mc_arr[%j%]!
set neo=!neo_arr[%j%]!

echo.
echo ============= Build #%j ====================
echo Minecraft-Version: %mc%
echo NeoForge-Version: %neo%
echo ============================================
call gradlew build -Pminecraft_version=%mc% -Pneo_version=%neo%
if errorlevel 1 (
    echo Build #%j mit %mc%/%neo% ist FEHLGESCHLAGEN!
) else (
    echo Build #%j mit %mc%/%neo% erfolgreich!
)
set /a j+=1
goto build_loop

:done
echo.
echo Alle Builds abgeschlossen.
pause