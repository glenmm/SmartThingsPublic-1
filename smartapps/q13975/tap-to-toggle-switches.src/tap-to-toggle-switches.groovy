/**
 *  tap to toggle switches
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
    name: "Tap to toggle switches",
    namespace: "q13975",
    author: "Mike Wang",
    description: "Double tap a switch to toggle other switches",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

def appVersion() { "1.0.0" }
def appVerDate() { "2-10-2017" }

preferences {
	section("Double Tap this switch") {
		input name: "master", type: "capability.switch", title: "Master Switch?", required: true
	}
	section("to toggle switches") {
		input name: "slaves", type: "capability.switch", title: "Slave Switch?", required: true, multiple: true
	}
	section("It will toggle the majority switches if some are on or off") {
		input name: "tmode", type: "bool", title: "Or it will toggle every switch if checked", required: true, defaultValue: false
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
	state.nextTime = 0
	subscribe(master, "switch.on", switchHandler, [filterEvents: false])
	subscribe(master, "switch.off", switchHandler, [filterEvents: false])
}

def switchHandler(evt) {
	if(!evt.isStateChange()) {
		def eventTime = evt.date.getTime()
		if(state.nextTime < eventTime) {	// first tap
			// set time fence for second tap
			state.nextTime = eventTime + 5000
		} else {				// second tap
			state.nextTime = 0	
			toggleSwitches(tmode)
		}
	} else if(state.nextTime) {
		state.nextTime = 0	
	}
}

private toggleSwitches(tm) {
	def onSwitches = []
	def offSwitches = []
	slaves?.each {	
		if(it.currentSwitch == "on") {
			onSwitches << it
		} else {
			offSwitches << it
		}
	}
	if(tm) {
		onSwitches?.each { it.off() }
		offSwitches?.each { it.on() }
	} else if(onSwitches?.size() >= offSwitches?.size()) {
		onSwitches?.each { it.off() }
	} else {
		offSwitches?.each { it.on() }
	}	
}
