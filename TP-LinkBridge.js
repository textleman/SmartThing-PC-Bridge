/*
TP-linkBridge.js V2.0.
This is a applet designed to interface to the TP-LinkBridge.groovy device handler.  This node provides a PC restart capability in the event that the TP-LinkServer applet hangs.

History:
04/07/2017 - Initial release as Version 2.0 (to match other items).
*/
//--------------------------------------------------------------------------------
var http = require('http')
var net = require('net')
var server = http.createServer(onRequest)
const bridgeExec = require('child_process').exec
var bridgePort = '8084' // Same as in NodeJSBridge.groovy
server.listen(bridgePort)
console.log("Node.JS Bridge")
//-- For each request received from the SmartThings. -----------------------------
function onRequest(request, response){
	console.log(" ")
	console.log(new Date())
	var command = request.headers["command"]
	if(command == "restartPC"){
		console.log("Bridge restarting")
		response.setHeader("cmd-response", "restart")
		response.end()
		bridgeExec('shutdown /r /t 005')
	} else if(command == "pollBridge"){
		response.setHeader("cmd-response", "ok")
		console.log("Bridge poll response sent to SmartThings")
		response.end()
	} else {
		console.log("Unexpected command received from SmartThings")
		response.end()
	}
}