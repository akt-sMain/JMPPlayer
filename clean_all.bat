@echo off
set /p ANS="Initialize to the state at the time of installation.(Y/N)?"

if /i {%ANS%}=={y} (goto :yes)
if /i {%ANS%}=={yes} (goto :yes)

EXIT

:yes
rd /s /q plugins
rd /s /q zip
del skin.txt
del activate
rem clean.bat
rd /s /q output
del config.txt
del history.txt

pause