# SmartThing-PC-Bridge

This series provides one of two bridges to a SmartThings associated PC Bridge running Windows 10.

  a. SoloBridge - runs and interfaces to a single node.js applet on the PC Bridge.
  
  b. TP-LinkBridge - interfaces to two node.js applets on the PC Bridge (the bridge and the server nodes).
  
Functionality:
  1.  Poll the interfaced node.js appilets for operational state.
  
  2.  Restart the Bridge PC (sends to command to all interfaced applets, so if only one is operating it will still cause a restart.
  
There are two versions of the bridge:

  SoloBridge.groovy.  Interfaces to the single TP-LinkBridge.js node applet.
  
  TP-LinkBridge.groovy.  Interfaces to the TP-LinkBridge.js and the device controller applet TP-LinkServer.js.

Installation.  See TP-Link Bridge Install.txt
