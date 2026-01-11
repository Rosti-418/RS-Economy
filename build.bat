@echo off
setlocal enabledelayedexpansion

echo =============================================
echo Minecraft Mod Multi-Version Builder
echo =============================================

rem --- Standard-Versionen verwenden (problematische wurden entfernt)
echo Verwende alle verfügbaren Versionen mit version-spezifischen Implementierungen
echo Minecraft: 1.20.6,1.21.0,1.21.1,1.21.2,1.21.3,1.21.4,1.21.5,1.21.6,1.21.7,1.21.8
set mc_versions=1.20.6,1.21.0,1.21.1,1.21.2,1.21.3,1.21.4,1.21.5,1.21.6,1.21.7,1.21.8
set neo_versions=20.6.139,21.0.167,21.1.218,21.2.1-beta,21.3.95,21.4.156,21.5.96,21.6.20-beta,21.7.25-beta,21.8.52
rem NeoForge 1.21.9+ haben bekannte Zip-Fehler im Build-System - werden daher nicht gebaut

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
echo Hinweis: Versionen mit bekannten NeoForge-Problemen wurden entfernt
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