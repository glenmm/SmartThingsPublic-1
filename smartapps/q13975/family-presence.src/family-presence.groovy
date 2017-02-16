/**
 *  family presence
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
    name: "family presence",
    namespace: "q13975",
    author: "Mike Wang",
    description: "Determine family presence ",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

def appVersion() { "1.0.0" }
def appVerDate() { "2-16-2017" }

preferences {
	section("Family resident presence") {
		input name: "familyResident", type: "capability.presenceSensor", title: "presence sensor", required: true
	} 
	section("Sensors detecting presence") {
		input name: "residentMotion", type: "capability.motionSensor", title: "motion sensors", multiple: true, required: true
	}
	section("When family members are not present") {
		input name: "familyMember", type: "capability.presenceSensor", title: "presence sensor", multiple: true, required: false
	} 
	section("Not in mode") {
		input name: "excludeMode", type: "mode", title: "select mode(s)", multiple: true, required: false
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
	subscribe(residentMotion, "motion", residentMotionHandler)
}

def residentMotionHandler(evt) {
	if(!excludeMode?.contains(location.mode) && !familyMember?.currentPresence.contains("present")) {
		if(evt.value == "active" || residentMotion.currentMotion.contains("active")) {
			if(familyResident.currentValue != "present" ) {
				familyResident.arrived()
			}
		} else {
			familyResident.departed()
		}
	}
}
