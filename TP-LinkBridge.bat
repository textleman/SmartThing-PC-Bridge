REM  Place this file in the TP-Link Bulb top level directory.
REM  Add path to the bridge directory if auto-starting.
cd c:\1-TP Link\
color 3f
title TP-Link_Bridge
prompt $_
Echo off
CLS
:startNode
date /t
time /t
node TP-LinkBridge.js
goto startNode