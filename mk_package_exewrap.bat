REM exewrap.exeを別途ダウンロードすること

set EXEWRAP_PATH=exewrap.exe
set ICON_PATH=icon.ico
set JAR_SRC_PATH=project\JMP\JMPPlayer.jar
set EXE_SRC_PATH=project\JMP\JMPPlayer.exe
set EXE_DST_PATH=JMPPlayer.exe
set JRE_CFG=APPDIR

echo exewrapパッケージ作成
%EXEWRAP_PATH% -g -l %JRE_CFG% -i %ICON_PATH% -t 1.8 -e NOLOG -j %JAR_SRC_PATH%
move %EXE_SRC_PATH% %EXE_DST_PATH%

call mk_package.bat
REM java -jar lib/flib.jar -unzip jre.zip JMPPlayer
call powershell -command "Expand-Archive -Force jre.zip JMPPlayer"

pause