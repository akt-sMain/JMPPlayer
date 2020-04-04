md JamPlayer

copy JamPlayer.exe JamPlayer\JamPlayer.exe
copy project\JMP\JamPlayer.jar JamPlayer\JamPlayer.jar
copy clean.bat JamPlayer\clean.bat
copy clean.bat JamPlayer\clean_all.bat
xcopy /e project\JMP\skin JamPlayer\skin

pause