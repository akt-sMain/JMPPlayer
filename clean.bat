@echo off
set /p ANS="Initialize the configuration file.(Y/N)?"

if /i {%ANS%}=={y} (goto :yes)
if /i {%ANS%}=={yes} (goto :yes)

EXIT

:yes
rd /s /q output
del save\config.txt
del save\history.txt

pause