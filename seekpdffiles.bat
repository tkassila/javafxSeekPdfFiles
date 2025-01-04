@echo off
rem SET JAVA_HOME=C:\Users\tuoma\javafx-sdk-11.0.2
rem SET PATH=%JAVA_HOME%\bin;%PATH%
ECHO PATH %PATH%
java -version
rem # exit 0
rem  -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.win.uiScaleX=2.5 -Dsun.java2d.win.uiScaleY=2.5 
rem echo java -cp .\seekpdffiles.jar;%CLASSPATH% com.metait.javafxseekpdffiles.SeekPdfFilesApplication 
java --module-path "C:\Users\tuoma\javafx-sdk-11.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp .\javafxSeekPdfFiles.jar;%CLASSPATH% com.metait.javafxseekpdffiles.SeekPdfFilesApplication 
rem DISPLAY=:0 java -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.win.uiScaleX=2.5 -Dsun.java2d.win.uiScaleY=2.5 -cp ./javafxSeekPdfFiles.jar:$CLASSPATH com.metait.javafxseekpdffiles.SeekPdfFilesApplication $*

