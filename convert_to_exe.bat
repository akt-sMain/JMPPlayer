@echo off

REM exewrap.exe��ʓr�_�E�����[�h���邱��
exewrap.exe -g -i icon.ico -t 1.8 -e NOLOG -j project\JMP\JamPlayer.jar
move project\JMP\JamPlayer.exe JamPlayer.exe
exit