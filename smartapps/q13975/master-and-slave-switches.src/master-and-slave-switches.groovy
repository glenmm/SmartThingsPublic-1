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
    description: "A master switch can turn on and off all slave switches. When all slave switches turned on or off, the master switch will turn on and off accordingly. ",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Define a master switch:") {
		input "master", "capability.switch", required: true
	}
    section("Define slave switches:") {
    	input "slaves", "capability.switch", multiple: true, required: true
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(master, "switch.on", handlerMasterOn, [filterEvents: false])
	subscribe(master, "switch.off", handlerMasterOff, [filterEvents: false])
	subscribe(slaves, "switch.on", handlerSlavesOn, [filterEvents: false])
	subscribe(slaves, "switch.off", handlerSlavesOff, [filterEvents: false])
}

// Handler when master switch is turned on
def handlerMasterOn(evt) {
	log.debug "Master switch was turned $evt.value"

    def offSwitches = getSwitchesByState(slaves, "off")
	offSwitches?.each { it ->
    	it.on()
    }
}

// Handler when master switch is turned off
def handlerMasterOff(evt) {
	log.debug "Master switch was turned $evt.value"
    
    def onSwitches = getSwitchesByState(slaves, "on")
    onSwitches?.each { it ->
    	it.off()
    }
}

// Handler when slave switch is turned on
def handlerSlavesOn(evt) {
	log.debug "Slave swtich was turned $evt.value"
	
    def onSwitches = getSwitchesByState(slaves, "on")
    if(onSwitches?.size() == slaves.size() && master.currentSwitch == "off") {
    	master.on()
    }
}

// Handler when slave switch is turned off
def handlerSlavesOff(evt) {
	log.debug "Slave swtich was turned $evt.value"
    
    def offSwitches = getSwitchesByState(slaves, "off")
	if(offSwitches?.size() == slaves.size() && master.currentSwitch == "on") {
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