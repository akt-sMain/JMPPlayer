REM exewrap.exeを別途ダウンロードすること

set EXEWRAP_PATH=exewrap.exe
set ICON_PATH=icon.ico
set JAR_SRC_PATH=project\JMP\JamPlayer.jar
set EXE_SRC_PATH=project\JMP\JamPlayer.exe
set EXE_DST_PATH=JamPlayer.exe

echo exewrapパッケージ作成
%EXEWRAP_PATH% -g -i %ICON_PATH% -t 1.8 -e NOLOG -j %JAR_SRC_PATH%
move %EXE_SRC_PATH% %EXE_DST_PATH%

call mk_package.bat

pause