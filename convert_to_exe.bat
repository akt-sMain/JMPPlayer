@echo off

REM exewrap.exeを別途ダウンロードすること
exewrap.exe -g -i icon.ico -t 1.8 -e NOLOG -j project\JMP\JamPlayer.jar
move project\JMP\JamPlayer.exe JamPlayer.exe
exit