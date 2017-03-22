/**
 *  Master and Slave Switches
 *
 *  Copyright 2017 Mike Wang
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

definition(
	name: "Master and Slave Switches",
	namespace: "q13975",
	author: "Mike Wang",
	description: "Master and slave switch(es) can work together as a set",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

def appVersion() { "1.0.0" }
def appVerDate() { "3-19-2017" }

preferences {
	section("Define master switch") {
		input name: "master", type: "capability.switch", title: "Master Switch?", required: true
	}
	section("to manage slave switches") {
		input name: "slaves", type: "capability.switch", title: "Slave Switches?", multiple: true, required: true
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	// handlers
	subscribe(master, "switch.on", handlerMasterOn, [filterEvents: false])
	subscribe(master, "switch.off", handlerMasterOff, [filterEvents: false])
	subscribe(slaves, "switch.on", handlerSlavesOn, [filterEvents: false])
	subscribe(slaves, "switch.off", handlerSlavesOff, [filterEvents: false])
}

// Handler when master switch is turned on
def handlerMasterOn(evt) {
	if(evt.isStateChange()){
		slaves.on()
	}
}

// Handler when master switch is turned off
def handlerMasterOff(evt) {
	if(evt.isStateChange()){
		slaves.off()
	}
}

// Handler when slave switch is turned on
def handlerSlavesOn(evt) {
	if(evt.isStateChange() && master.currentSwitch != "on" && !slaves.currentSwitch.contains("off")) {
		master.on()
	}
}

// Handler when slave switch is turned off
def handlerSlavesOff(evt) {
	if(evt.isStateChange() && master.currentSwitch != "off" && !slaves.currentSwitch.contains("on")) {
		master.off()
	}
}
