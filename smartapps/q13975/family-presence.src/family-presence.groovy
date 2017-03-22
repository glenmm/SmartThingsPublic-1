/**
 *  Family presence
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
    name: "Family presence",
    namespace: "q13975",
    author: "Mike Wang",
    description: "Determine family resident present",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

def appVersion() { "1.0.0" }
def appVerDate() { "3-21-2017" }

preferences {
	section("Affiliated family resident") {
		input name: "affiliateResident", type: "capability.presenceSensor", title: "affiliated presence sensor", required: true
	} 
	section("When sensors detecting motions") {
		input name: "residentMotion", type: "capability.motionSensor", title: "motion sensors", multiple: true, required: true
	}
	section("And optionally when master residents are not home") {
		input name: "masterResident", type: "capability.presenceSensor", title: "master presence sensors", multiple: true, required: false
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
	if(!masterResident?.currentPresence.contains("present")) {
		if(evt.value == "active") {
			if(affiliateResident.currentValue != "present" ) {
				affiliateResident.arrived()
			}
		} else if(!residentMotion.currentMotion.contains("active") && affiliateResident.currentValue != "not present" ) {
			affiliateResident.departed()
		}
	} else if(affiliateResident.currentValue != "not present" ) {
		affiliateResident.departed()
	}
}

