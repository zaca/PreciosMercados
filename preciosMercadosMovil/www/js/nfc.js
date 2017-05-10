/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

writeStatus = false;

var app = {

	dateFormat : false,
	writeStatus : false,

	// Application Constructor
	initialize : function() {
		document.addEventListener('deviceready', this.onDeviceReady.bind(this),
				false);

		// alert("Initialized");
	},

	// deviceready Event Handler
	//
	// Bind any cordova events here. Common events are:
	// 'pause', 'resume', etc.
	onDeviceReady : function() {
		this.loadSettings();
		this.receivedEvent('deviceready');
		inputButton = document.getElementById("saveButton");
		inputButton.addEventListener('click', this.saveButtonClick, false);

		inputValue = document.getElementById("addValueButton");
		inputValue.addEventListener('click', this.addValueButtonPressed, false);

		searchButton = document.getElementById("searchButton");
		searchButton.addEventListener('click', this.searchButtonClick, false);

		searchButton = document.getElementById("idInputValue");
		searchButton.addEventListener('keydown', this.nextKeyPressed);

		document.addEventListener("backbutton", this.onBackKeyDown.bind(this),
				false);

		if (typeof nfc !== "undefined") {
			this.setNfcEventListener();
		} else {
			// fail gracefully
			fail();
		}
	},

	loadSettings : function() {
		var storage = window.localStorage;
		dateFormat = storage.getItem("dateFormat");
	},

	// Update DOM on a Received Event
	receivedEvent : function(id) {
		// var parentElement = document.getElementById(id);
		// var listeningElement = parentElement.querySelector('.listening');
		// var receivedElement = parentElement.querySelector('.received');

		// listeningElement.setAttribute('style', 'display:none;');
		// receivedElement.setAttribute('style', 'display:block;');
		console.log('Received Event: ' + id);
	},

	setWriteStatusTrue : function() {
		writeStatus = true;
		// document.getElementById("readingTag").style.display="block";
	},

	setWriteStatusFalse : function() {
		writeStatus = false;
		// document.getElementById("readingTag").style.display="none";
	},

	searchButtonClick : function() {
		key = document.getElementById("idInputKey").value;
		valueHolder = document.getElementById("displayValues");
		var storage = window.localStorage;
		var value = storage.getItem(key); // Pass a key name to get its value.
		if (value != undefined) {
			valueHolder.value = value;
		} else {
			valueHolder.value = "Ingrese un valor";
		}
	},

	saveButtonClick : function() {
		key = document.getElementById("idInputKey").value;
		value = document.getElementById("idInputValue").value;
		// var value = storage.getItem(key);
		// var storage = window.localStorage;
		// storage.setItem(key, value);
		saveNote(key, value);
		displayValues(loadNotes(key));
		app.setWriteStatusTrue();
		alert("grabe");
	},

	addValueButtonPressed : function() {
		// document.getElementById("displayValues").select();
		document.getElementById("mainForm").style.display = "block";
		document.getElementById("idInputKey").value = "";
		document.getElementById("idInputValue").value = "";
		document.getElementById("idInputKey").focus();
	},

	saveTagPressed : function() {
		key = document.getElementById("idInputKey").value;
		var message = [ ndef.textRecord("SimpleNFCNotes"), ndef.textRecord(key) ];
		nfc.write(message, function() {
			alert("Tag grabado")
		}, function(e) {
			alert("Error al grabar: " + e)
		});
	},

	nextKeyPressed : function(event) {
		if (event.keyCode == 9) {
			// you got tab i.e "NEXT" Btn
			app.saveButtonClick();
		}
		if (event.keyCode == 13) {
			// you got enter i.e "GO" Btn
			app.saveButtonClick();
		}
	},

	onBackKeyDown : function() {
		if (document.getElementById("mainForm").style.display == "block") {
			document.getElementById("mainForm").style.display = "none";
		}
		if (document.getElementById("InputForm").style.display == "block") {
			document.getElementById("saveButton").focus();
			document.getElementById("InputForm").style.display == "none";
		}
	},

	setNfcEventListener : function() {
		// event
		nfc.addNdefListener(function(nfcEvent) {
			var tag = nfcEvent.tag;
			ndefMessage = tag.ndefMessage;
			key = nfc.bytesToString(ndefMessage[1].payload.slice(3));
			document.getElementById("idInputKey").value = key;
			notes = loadNotes(key);
			if(notes != null){
				if (notes.isArray && notes.length > 0) {
					text = notes[0];
					for (i = 1; i < notes.legth; i++) {
						text += "\n" + notes[i];
					}
					document.getElementById("comentaries").innerHtml = text;
				}
			}else{
				document.getElementById("comentaries").innerHtml = "no data";
			}
			document.getElementById("mainForm").style.display = "block";
		},

		function() {
			// alert("Ready to read tags!");
			// failure
		},

		function() {
			alert("Por favor encienda NFC");
			navigator.app.exitApp();
		});

		nfc.addTagDiscoveredListener(function(nfcEvent) {
			var tag = nfcEvent.tag;
			message = nfc.bytesToHexString(tag.id);
			tagMessage = JSON.stringify(message);
			document.getElementById("idInputValue").value = tagMessage;
		},

		function() {
			// alert("addTagDiscoveredListener Success");
		},

		function() {
			alert("addTagDiscoveredListener Fail");
		});
	}
};

app.initialize();

function getCurrentTime() {
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth() + 1;// January is 0, so always add + 1

	var yyyy = today.getFullYear();
	if (dd < 10) {
		dd = '0' + dd
	}
	if (mm < 10) {
		mm = '0' + mm
	}
	today = mm + '/' + dd + '/' + yyyy;
	return today;
}

function saveNote(key, value) {
	notes = loadNotes(key);
	noteValue = key + "|" + value;
	if(notes != null){
		notes += "||" + noteValue;
	}else{
		notes = noteValue;		
	}
	var storage = window.localStorage;
	storage.setItem(key, JSON.stringify(notes));
}

function loadNotes(key) {
	var storage = window.localStorage;
	var value = storage.getItem(key);
	alert("esto es lo que grabe" + value);
	if(value != null){
		return value.split("||");
	}
	return value;
}

/*
 * function StoreItemData(key,value){ var storage = window.localStorage; var
 * storedValue = storage.getItem(key); // Pass a key name to get its value.
 * value = key + "|" + value + "|" + getCurrentTime(); if (storedValue !=
 * undefined) { value = storedValue + "||" + value; } storage.setItem(key,
 * value); } function loadItemData(key){ var storage = window.localStorage;
 * return storage.getItem(key).split("||"); // Pass a key name to get its value. }
 */
function displayValues(notes) {
	if (notes.isArray && notes.length > 0) {
		text = "<ul>";
		alert("tiene " + notes.length);
		for (i = 0; i < notes.legth; i++) {
			text += "<li>" + "aca iria fecha" + " " + "aca iria un comentario" + "</li>"
		}
		text += "</ul>";
		document.getElementById("comentaries").innerHTML = text;
	}else{
		var storage = window.localStorage;
		storage
		document.getElementById("comentaries").innerHTML = "Nothing save";
	}
}
