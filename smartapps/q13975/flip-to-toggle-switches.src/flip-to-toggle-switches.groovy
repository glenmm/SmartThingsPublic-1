/**
 *  Flip to toggle switches
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
    name: "Flip to toggle switches",
    namespace: "q13975",
    author: "Mike Wang",
    description: "Flip a switch to toggle other switches",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

def appVersion() { "1.0.0" }
def appVerDate() { "2-17-2017" }

preferences {
	section("Flip this switch") {
		input name: "master", type: "capability.switch", title: "Master Switch?", required: true
	}
	section("to toggle these switch") {
		input name: "slaves", type: "capability.switch", title: "Slave Switch?", required: true, multiple: true
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
	state.lastTime = 0
	subscribe(master, "switch", switchHandler, [filterEvents: false])
}

def switchHandler(evt) {
	if(wasFlipped(evt)) {
		toggleSwitches()
	}
}

private wasFlipped(evt) {
	def result = false
	if(evt.isStateChange() && (evt.value == "on" || evt.value == "off")) {
		def lastTime = evt.date.getTime() - 5000	
		def lastDate = lastTime > state.lastTime ? new Date(lastTime) : new Date(state.lastTime)
		def recentStates = master.events([all:true, max:10]).findAll{ it.name == "switch" && (it.value == "on" || it.value == "off") && it.date.after(lastDate) && !it.date.after(evt.date) }
		if(recentStates?.size() > 1 && recentStates[0].isStateChange() && recentStates[1].isStateChange() && recentStates[0].value != recentStates[1].value) {
			result = true
			state.lastTime = evt.date.getTime()
		}
	}
	result
}

private toggleSwitches() {
	if(slaves.currentSwitch.contains("on")) {
		slaves.off()
	} else {
		slaves.on()
	}	
}
