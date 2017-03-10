/**
 *  Switch auto off
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
    name: "Switch auto off",
    namespace: "q13975",
    author: "Mike Wang",
    description: "Turn off a switch automatically when it&#39;s turned on in certain time",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

def appVersion() { "1.0.0" }
def appVerDate() { "3-10-2017" }

preferences {
	section("Automatically turn off") {
		input name: "theSwitches", type: "capability.switch", title: "these switches", required: true, multiple: true
	}
	section("in seconds") {
		input name: "inSeconds", type: "number", title: "how much?", required: true, defaultValue: 3
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
	subscribe(theSwitches, "switch.on", switchOnHandler)
}

def switchOnHandler(evt) {
	try {
		def theSwitch = evt.device
		if(evt.isStateChange() && theSwitch) {
			runIn(inSeconds, theSwitch.off())
		}
	} catch (e) {
		log.debug("no evt.device exception occured for ${evt.name}", e) 
	}
}
