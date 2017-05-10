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
var app = {
		
	// Application Constructor
	initialize : function() {
		document.addEventListener('deviceready', this.onDeviceReady.bind(this),
				false);
	},

	// deviceready Event Handler
	//
	// Bind any cordova events here. Common events are:
	// 'pause', 'resume', etc.
	onDeviceReady : function() {
		// this.receivedEvent('deviceready');
		callButton = document.getElementById("callButton");
		callButton.addEventListener('click', this.callButtonClick, false);
		
		filterContainer = document.getElementById("filtersContainer");
		fillFilters(filterContainer);
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

	callButtonClick : function() {
		container = document.getElementById("serviceResult");
		var response = callRestService(container);
	}

};

app.initialize();

function callRestService(container) {
	var response = "";
	var url = "http://34.204.253.238:8080/concentrador/rest/quotation/byCode/123";
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
			if (xhr.status === 200) {
				lista = JSON.parse(this.responseText);
				visualization(lista, container);
			} else {
				container.innerHTML = xhr.statusText;
			}
		}
	};
	try {
		xhr.open('GET', url, true);
		xhr.send();
	} catch (err) {
		container.innerHTML = err.message;
	}
};

function visualization(arr, element) {
	var table = document.createElement("TABLE");
    table.setAttribute("id", "productsTable");
    table.setAttribute('class', 'contentTable');
    element.appendChild(table);
    
    /*Cabecera*/
    line = document.createElement("TR");
    line.setAttribute('class', 'contentLineHeader');
	table.appendChild(line);

	td1 = document.createElement("TD");
	cellContent1 = document.createTextNode("Descripcion");
	td1.appendChild(cellContent1);
	line.appendChild(td1);
	
	td2 = document.createElement("TD");
	cellContent2 = document.createTextNode("Precio maximo");
	td2.appendChild(cellContent2);
	line.appendChild(td2);
	
	td3 = document.createElement("TD");
	cellContent3 = document.createTextNode("Precio minimo");
	td3.appendChild(cellContent3);
	line.appendChild(td3);
	
	td4 = document.createElement("TD");
	cellContent4 = document.createTextNode("Zona");
	td4.appendChild(cellContent4);
	line.appendChild(td4);
    
    /*Contenido*/
    for (i = 0; i < arr.length && i < 10; i++) {
    	line = document.createElement("TR");
    	table.appendChild(line);

    	td1 = document.createElement("TD");
    	cellContent1 = document.createTextNode(arr[i].description);
    	td1.appendChild(cellContent1);
    	line.appendChild(td1);
    	
    	td2 = document.createElement("TD");
    	cellContent2 = document.createTextNode(arr[i].maxValue);
    	td2.appendChild(cellContent2);
    	line.appendChild(td2);
    	
    	td3 = document.createElement("TD");
    	cellContent3 = document.createTextNode(arr[i].minValue);
    	td3.appendChild(cellContent3);
    	line.appendChild(td3);
    	
    	td4 = document.createElement("TD");
    	cellContent4 = document.createTextNode(arr[i].source);
    	td4.appendChild(cellContent4);
    	line.appendChild(td4);
    }
};

