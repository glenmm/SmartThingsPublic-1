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

def appVersion() { "1.0.0" }
def appVerDate() { "2-7-2017" }

definition(
	name: "Tap to toggle a slave switch",
	namespace: "q13975",
	author: "Mike Wang",
	description: "Double clicking the master switch will toggle the slave switch",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	section("Define a master switch") {
		input name: "master", type: "capability.switch", title: "Master Switch?", required: true
	}
	section("Define a slave switch") {
		input name: "slave", type: "capability.switch", title: "Slave Switch?", required: true
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
	subscribe(master, "switch", switchHandler, [filterEvents: false])
}

def switchHandler(evt) {
	def recentEvents = master.eventsBetween(new Date(evt.date.getTime() - 5000), evt.date)?.findAll{it.name == "switch"}
	if(recentEvents?.size() > 1 && recentEvents[0].value == recentEvents[1].value) {
		if(evt.value == "on") {
			slave.on()
		} else {
			slave.off()
		}
	}
}
