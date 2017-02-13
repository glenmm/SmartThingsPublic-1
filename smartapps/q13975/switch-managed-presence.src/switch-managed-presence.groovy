/**
 *  Switch Managed Presence
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
    name: "Switch Managed Presence",
    namespace: "q13975",
    author: "Mike Wang",
    description: "Use a switch to manage a presence",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

def appVersion() { "1.0.0" }
def appVerDate() { "2-9-2017" }

preferences {
	section("Use this switch") {
		input name: "mySwitch", type: "capability.switch", title: "Select a switch?", required: true
	}
	section("to manage this presence") {
		input name: "myPresence", type: "capability.presenceSensor", title: "Select a presence?", required: true
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
	subscribe(mySwitch, "switch", switchHandler, [filterEvents: false])
}

def switchHandler(evt) {
	if(evt.isStateChange()) {
		if(evt.value == "on") {
			myPresence.arrived()
		} else if(evt.value == "off") {
			mypresence.departed()
		}
	}
}
