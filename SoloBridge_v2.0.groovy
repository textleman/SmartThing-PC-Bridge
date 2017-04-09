/*
TP-Link Bridge Device Handler

Copyright 2017 Dave Gutheinz

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this 
file except in compliance with the License. You may obtain a copy of the License at:

		http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under 
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 
ANY KIND, either express or implied. See the License for the specific language governing 
permissions and limitations under the License.

Notes: 
1.  This device handler provides the operational status of two node files on the
	TP-Link bridge used for controlling TP-Link devices.
    a.	Allows user to poll exact status or each node.
    b.	Allows user to restart the bridge PC (which will also restart the nodes).
2.	This DH uses two ports that are embedded within the code below.  These ports must
	be the same as in the two node applets.
    a.	serverPort (default = '8082') is the same in TP-LinkServer.js.
	n.	bridgePort (default = '8084') is the same in TP-LinkBridge.js.
3.	This DH is tested on a Windows 10 PC with auto-restart and auto-login enables
	as well as the 'TP-LinkBridge.bat' and 'TP-LinkServer.bat" files scheduled for
    starting at log-in in the Windows Scheduler.

Update History
	04-07-2017	- Initial release.
	04-08-2017	- Changed to V2.0 for commonality across line.
*/
metadata {
	definition (name: "TP-LinkBridge", namespace: "V2.0", author: "David Gutheinz") {
		capability "Bridge"
        capability "Refresh"
        attribute "refresh", "string"
        attribute "BridgeStatus", "string"
        attribute "ServerStatus", "string"
		command "pollBridge"
        command "pollServer"
		command "restartPC"
	}
	tiles {
		standardTile("BridgeStatus", "BridgeStatus", width: 1, height: 1,  decoration: "flat") {
			state "ok", label:"Bridge Running", action:"pollBridge", icon:"st.secondary.refresh", backgroundColor:"#00a0dc"
			state "polling", label:"Polling Bridge", action:"pollBridge", icon:"st.secondary.refresh", backgroundColor:"#e86d13"
			state "restarting", label:"Restarting Bridge", action:"pollBridge", icon:"st.secondary.refresh", backgroundColor:"#bc2323"
		}
		standardTile("ServerStatus", "ServerStatus", width: 1, height: 1,  decoration: "flat") {
			state "ok", label:"Server Running", action:"pollServer", icon:"st.secondary.refresh", backgroundColor:"#00a0dc"
			state "polling", label:"Polling Server", action:"pollServer", icon:"st.secondary.refresh", backgroundColor:"#e86d13"
			state "restarting", label:"Restarting Server", action:"pollServer", icon:"st.secondary.refresh", backgroundColor:"#bc2323"
		}
		standardTile("RestartPC", "Restart PC", width: 1, height: 1, decoration: "flat") {
			state "RestartPC", label:'RESTART PC', action:"restartPC", backgroundColor:"#ffffff"
		}
		main("BridgeStatus")
		details(["BridgeStatus", "ServerStatus", "RestartPC", "RestartNodes"])
    }
}
preferences {
	input("bridgeIP", "text", title: "Bridge IP", required: true, displayDuringSetup: true)
}

def refresh() {
	log.info "Polling Bridge and Server Nodes at $bridgeIP"
    sendCmdtoBridge('pollBridge')
	sendCmdtoServer('pollServer')
}
def pollBridge() {
	log.info "Polling Bridge Node at $bridgeIP"
 	sendEvent(name: "BridgeStatus", value: "polling", isStateChange: true)
    sendCmdtoBridge('pollBridge')
}
def pollServer() {
	log.info "Polling Server Node at $bridgeIP"
 	sendEvent(name: "ServerStatus", value: "polling", isStateChange: true)
	sendCmdtoServer('pollServer')
}
def restartPC() {
	log.info "Restarting Bridge PC at $bridgeIP"
	sendCmdtoServer('restartPC')
	sendCmdtoBridge('restartPC')
}
private sendCmdtoServer(command){
	def headers = [:]
    def serverPort = '8082'  // Must be same as in server node js file.
	headers.put("HOST", "$bridgeIP:$serverPort")
	headers.put("command", command)
	sendHubCommand(new physicalgraph.device.HubAction(
		[headers: headers],
 		device.deviceNetworkId,
 		[callback: serverResponse]
	))
}
private sendCmdtoBridge(command){
	def headers = [:]
    def bridgePort = '8084' // Must be same as in NodeJSBridge/js.
	headers.put("HOST", "$bridgeIP:$bridgePort")
	headers.put("command", command)
	sendHubCommand(new physicalgraph.device.HubAction(
		[headers: headers],
 		device.deviceNetworkId,
 		[callback: bridgeResponse]
	))
}
def bridgeResponse(response){
	def cmdResponse = response.headers["cmd-response"]
	if (cmdResponse == 'ok') {
		log.info "NodeJS Bridge Node at $bridgeIP is operational"
		sendEvent(name: "BridgeStatus", value: "ok", isStateChange: true)
	}else if (cmdResponse == 'restartPC') {
		log.info "Bridge PC at $bridgeIP is restarting"
 		sendEvent(name: "ServerStatus", value: "restarting", isStateChange: true)
 		sendEvent(name: "BridgeStatus", value: "restarting", isStateChange: true)
	} else {
		log.info "Invalid response received from Bridge Node on $bridgeIP"
	}
}
def serverResponse(response){
	def cmdResponse = response.headers["cmd-response"]
	if (cmdResponse == 'ok') {
		log.info "NodeJS Server Node at $bridgeIP is operational"
		sendEvent(name: "ServerStatus", value: "ok", isStateChange: true)
	}else if (cmdResponse == 'restartPC') {
		log.info "Bridge PC at $bridgeIP is restarting"
 		sendEvent(name: "ServerStatus", value: "restarting", isStateChange: true)
 		sendEvent(name: "BridgeStatus", value: "restarting", isStateChange: true)
	} else {
		log.info "Invalid response received from Server Node on $bridgeIP"
	}
}