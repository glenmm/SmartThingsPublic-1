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
    description: "A master switch can turn on and off all slave switches. When all slave switches are on, master will be on. When any or all slave switches are off, the master switch will be off accordingly.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

def appVersion() { "1.0.0" }
def appVerDate() { "2-6-2017" }

preferences {
	section("Define a master switch") {
		input name: "master", type: "capability.switch", title: "Master Switch?", required: true
	}
    section("Define slave switches") {
    	input name: "slaves", type: "capability.switch", title: "Slave Switches?", multiple: true, required: true
    }
    section("Master switch will be off if any slaves are off") {
    	input name: "masterOffAtAll", type: "bool", title: "or until all slaves are off if checked", defaultValue: false
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
	// Initialize some variables
	state.slaveTriggerOn = false
	state.slaveTriggerOff = false

	// handlers
	subscribe(master, "switch.on", handlerMasterOn, [filterEvents: false])
	subscribe(master, "switch.off", handlerMasterOff, [filterEvents: false])
	subscribe(slaves, "switch.on", handlerSlavesOn, [filterEvents: false])
	subscribe(slaves, "switch.off", handlerSlavesOff, [filterEvents: false])
}

// Handler when master switch is turned on
def handlerMasterOn(evt) {
	if(state.slaveTriggerOn) {
	   	state.slaveTriggerOn = false
    } else {
    	def offSwitches = getSwitchesByState(slaves, "off")
        offSwitches?.each { it ->
        	it.on()
        }
    }
}

// Handler when master switch is turned off
def handlerMasterOff(evt) {
	if(state.slaveTriggerOff) {
	   	state.slaveTriggerOff = false
    } else {
    	def onSwitches = getSwitchesByState(slaves, "on")
        onSwitches?.each { it ->
           	it.off()
        }
    }
}

// Handler when slave switch is turned on
def handlerSlavesOn(evt) {
    def onSwitches = getSwitchesByState(slaves, "on")
    if(onSwitches?.size() == slaves.size() && master.currentSwitch == "off") {
    	state.slaveTriggerOn = true
		master.on()
    }
}

// Handler when slave switch is turned off
def handlerSlavesOff(evt) {
   	def masterOff = true
	if(masterOffAtAll) {
    	def offSwitches = getSwitchesByState(slaves, "off")
        if(!(offSwitches?.size() == slaves.size())) {
        	masterOff = false
        }
	}
    if(masterOff && master.currentSwitch == "on") {
		state.slaveTriggerOff = true
	    master.off()
    }
}

// get a subset from a List of swithes by its state
private getSwitchesByState(switches, st="on") {
	if(switches) {
		st = st?.toLowerCase() == "on" ? "on" : "off"
    	def result = switches.findAll { it ->
	    	it.currentSwitch == st ? true : false
    	}
    }
}