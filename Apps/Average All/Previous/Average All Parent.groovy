/**
 *  Design Usage:
 *  This is the 'Parent' app for weather switch
 *
 *
 *  Copyright 2018 Andrew Parker
 *  
 *  This SmartApp is free!
 *
 *  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://www.paypal.me/smartcobra
 *  
 *
 *  I'm very happy for you to use this app without a donation, but if you find it useful
 *  then it would be nice to get a 'shout out' on the forum! -  @Cobra
 *  Have an idea to make this app better?  - Please let me know :)
 *
 *  
 *
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *-------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @Cobra
 *
 *-------------------------------------------------------------------------------------------------------------------
 *
 *  Last Update: 11/09/2018
 *
 *  Changes:
 *
 * 
 *
 *  
 *  
 *  V1.0.1 - added revised first page
 *  V1.0.0 - POC
 *
 */



definition(
    name:"Average All",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Parent App for Average All ChildApps ",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
    )







preferences {
	
     page name: "mainPage", title: "", install: true, uninstall: true
     
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
	version()
   
    log.info "There are ${childApps.size()} child apps"
    childApps.each {child ->
    log.info "Child app: ${child.label}"
    }
    
}


def mainPage() {
    dynamicPage(name: "mainPage") {
      installCheck()
        
if(state.appInstalled == 'COMPLETE'){
			display()

  section (""){
		app(name: "averageApp", appName: "Average All Child", namespace: "Cobra", title: "<b>Add a new 'Average' automation</b>", multiple: true)
            }
    section (" "){}
  section("App name") {
        label title: "Enter a name for parent app (optional)", required: false
            }    
	}
  }
}



def installCheck(){         
   state.appInstalled = app.getInstallationState() 
  if(state.appInstalled != 'COMPLETE'){
section{paragraph "Please hit 'Done' to install '${app.label}' parent app "}
  }
    else{
 //       log.info "Parent Installed OK"
    }
	}

def version(){
	unschedule()
	schedule("0 0 9 ? * FRI *", updateCheck) //  Check for updates at 9am every Friday
	updateCheck()  
}


def display(){
	if(state.status){
	section{paragraph "<img src='http://update.hubitat.uk/icons/cobra3.png''</img> Version: $state.version <br><font face='Lucida Handwriting'>$state.Copyright </font>"}
	if(state.status != "Current"){
	section{ 
	paragraph "$state.status"
	paragraph "$state.UpdateInfo"
    }
    }
}
}


def updateCheck(){
    setVersion()
	def paramsUD = [uri: "http://update.hubitat.uk/cobra.json"]
       	try {
        httpGet(paramsUD) { respUD ->
 //  log.warn " Version Checking - Response Data: ${respUD.data}"   // Troubleshooting Debug Code 
       		def copyrightRead = (respUD.data.copyright)
       		state.Copyright = copyrightRead
            def newVerRaw = (respUD.data.versions.Application.(state.InternalName))
            def newVer = (respUD.data.versions.Application.(state.InternalName).replace(".", ""))
       		def currentVer = state.version.replace(".", "")
      		state.UpdateInfo = (respUD.data.versions.UpdateInfo.Application.(state.InternalName))
                state.author = (respUD.data.author)
           
		if(newVer == "NLS"){
            state.status = "<b>** This app is no longer supported by $state.author  **</b>"       
            log.warn "** This app is no longer supported by $state.author **"      
      		}           
		else if(currentVer < newVer){
        	state.status = "<b>New Version Available (Version: $newVerRaw)</b>"
        	log.warn "** There is a newer version of this app available  (Version: $newVerRaw) **"
        	log.warn "** $state.UpdateInfo **"
       		} 
		else{ 
      		state.status = "Current"
      		log.info "You are using the current version of this app"
       		}
      					}
        	} 
        catch (e) {
        	log.error "Something went wrong: CHECK THE JSON FILE AND IT'S URI -  $e"
    		}
 	
}

def setVersion(){
		state.version = "1.0.1"	 
		state.InternalName = "AverageAllparent"  
}




