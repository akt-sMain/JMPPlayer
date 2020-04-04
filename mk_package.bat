md JamPlayer

copy JamPlayer.exe JamPlayer\JamPlayer.exe
copy project\JMP\JamPlayer.jar JamPlayer\JamPlayer.jar
copy clean.bat JamPlayer\clean.bat
copy clean_all.bat JamPlayer\clean_all.bat
xcopy /y /e skin JamPlayer\skin\

pause