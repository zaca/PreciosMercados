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

	host : "http://34.204.253.238:8080",
	//host : "http://localhost:8080",

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
		loadMarkets();
		loadProductTypes()

		var searchButton = document.getElementById("searchButton");
		searchButton.addEventListener('click', this.searchButtonClick, false);
/*
		var textInput = document.getElementById("filterText");
		textInput.addEventListener('onFocus', this
				.textFilterSelectContent(this), false);
		textInput.addEventListener('onClick', this
				.textFilterSelectContent(this), false);
		textInput.addEventListener('keydown', this.nextKeyPressed);
		$('#filterText').focus( this.textFilterSelectContent($('#filterText')));
 */	
		$('#filterText').click(this.textFilterSelectContent($('#filterText')));
		$('#filterText').keydown(this.nextKeyPressed);

		$('#configButton').click(this.configButtonClick);

		var filterContainer = document.getElementById("filtersSelectContainer");
		fillFilters(filterContainer);

		var closeConfigButton = document.getElementById("closeConfigButton");
		closeConfigButton.addEventListener('click',
				this.closeConfigButtonClick, false);

		// var select = document.getElementById("filterSelect");
		// select.addEventListener("change", filtersChange, false);
	},

	// Update DOM on a Received Event
	receivedEvent : function(id) {
		console.log('Received Event: ' + id);
	},

	searchButtonClick : function() {
		container = document.getElementById("serviceResult");
		var filter = document.getElementById("filterSelect").value;
		filterText = document.getElementById("filterText").value;
		if ("" != filterText) {
			filter = filterText.toUpperCase();
			;
		}
		var response = callPostService(container, filter);
	},

	configButtonClick : function() {
		principal = document.getElementById("principal");
		config = document.getElementById("configContainer");

		loadPreferences();

		principal.style.display = "none";
		config.style.display = "block";
	},

	textFilterSelectContent : function(element) {
		console.log(element.value);
		element.select();
	},

	nextKeyPressed : function(event) {
		if (event.keyCode == 9) {
			// you got tab i.e "NEXT" Btn
			app.searchButtonClick();
		}
		if (event.keyCode == 13) {
			// you got enter i.e "GO" Btn
			app.searchButtonClick();
		}
	},

	closeConfigButtonClick : function() {
		principal = document.getElementById("principal");
		config = document.getElementById("configContainer");

		savePreferences();

		principal.style.display = "block";
		config.style.display = "none";

	}

};

app.initialize();

function drawMarketSelector(id, description) {
	var selectorContainer = document.getElementById("marketsSelectorContainer");
	selectorContainer.innerHTML = selectorContainer.innerHTML
			+ "<div><input value='" + id + "' id='checkbox" + id
			+ "' name='checkboxMercados' type='checkbox'>"
			+ "<label id='labelcheckbox" + id + "' for='checkbox" + id
			+ "' class='configLabel'>" + description + "</label></div>"
}

function drawProductSelector(id, description) {
	var selectorContainer = document
			.getElementById("productsSelectorContainer");
	selectorContainer.innerHTML = selectorContainer.innerHTML
			+ "<div><input value='" + id + "' id='checkbox" + id
			+ "' name='checkboxProductos' type='checkbox'>"
			+ "<label id='labelcheckbox" + id + "' for='checkbox" + id
			+ "' class='configLabel'>" + description + "</label></div>"
}

function loadMarkets() {
         $.getJSON(app.host + "/concentrador/rest/quotation/listMarket",function(lista) {
        	 localstorage.setItem("marketsServerList", lista);
				for (i = 0; i < lista.length; i++) {
					localstorage.setItem(lista[i].id, lista[i].description);
					drawMarketSelector(lista[i].id, lista[i].description);
				}         
          });
}

function loadProductTypes() {
	var response = "";
	var url = app.host + "/concentrador/rest/quotation/listProductTypes";
	var xhr = new XMLHttpRequest();
	localstorage = window.localStorage;
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
			if (xhr.status === 200) {
				var lista = JSON.parse(this.responseText);
				localstorage.setItem("productsServerList", lista);
				for (i = 0; i < lista.length; i++) {
					localstorage.setItem(lista[i].id, lista[i].description);
					drawProductSelector(lista[i].id, lista[i].description);
				}

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
}

function loadFilters(container) {
	var response = "";
	var url = app.host + "/concentrador/rest/quotation/listCodes";
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
			if (xhr.status === 200) {
				lista = JSON.parse(this.responseText);
				drawFilters(lista, container);
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
}

function fillFilters(container) {
	var response = "";
	var url = app.host + "/concentrador/rest/quotation/listCodes";
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
			if (xhr.status === 200) {
				lista = JSON.parse(this.responseText);
				drawFilters(lista, container);
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
}

function callRestService(container, value) {
	var response = "";
	var url = app.host + "/concentrador/rest/quotation/byFilter/" + value;
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
			if (xhr.status === 200) {
				lista = JSON.parse(this.responseText);
				drawServiceResult(lista, container);
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

function callPostService(container, value) {

	localstorage = window.localStorage;
	var paramProducts = [];
	var paramMarkets = [];

	var markets = JSON.parse(localstorage.getItem("markets"));
	if (markets != null) {
		var marketsList = markets.markets;
		for (i = 0; i < marketsList.length; i++) {
			market = marketsList[i];
			if (market.checked == true) {
				paramMarkets.push(market.id);
			}
		}
	}

	var productTypes = JSON.parse(localstorage.getItem("products"));
	if (productTypes != null) {
		var productTypesList = productTypes.productTypes;
		for (i = 0; i < productTypesList.length; i++) {
			product = productTypesList[i];
			if (product.checked == true) {
				paramProducts.push(product.id);
			}
		}
	}

	// params.products = paramProducts;
	var params = {};
	var quote = {};
	quote.code = value;
	params.quotes = quote;
	params.products = paramProducts;
	params.markets = paramMarkets;
	var xhr = new XMLHttpRequest();
	var url = app.host + "/concentrador/rest/quotation/byFilter";
	var response = "";
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
			if (xhr.status === 200) {
				console.log(JSON.stringify(this.responseText));
				lista = JSON.parse(this.responseText);
				drawServiceResult(lista, container);
			} else {
				container.innerHTML = xhr.statusText;
			}
		}
	};
	try {
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type",
				"application/json; charset=ISO-8859-1");
		console.log(JSON.stringify(params));
		xhr.send(JSON.stringify(params));
	} catch (err) {
		container.innerHTML = err.message;
	}
};

function drawNoSelectionResult(element) {
	element.innerHTML = "No se encuentran configuraciones.";
}

function drawServiceResult(arr, element) {
	localstorage = window.localStorage;
	while (element.firstChild) {
		element.removeChild(element.firstChild);
	}

	var mercado = "";

	/* Contenido */
	for (i = 0; i < arr.length; i++) {
		if (arr[i].market != mercado) {
			h2 = document.createElement("h2");
			mercado = arr[i].market;
			h2Content = document.createTextNode(localstorage
					.getItem(arr[i].market));
			h2.appendChild(h2Content);
			h2.setAttribute('class', 'subtitle');
			element.appendChild(h2);

			var table = document.createElement("TABLE");
			table.setAttribute("id", "productsTable");
			table.setAttribute('class', 'contentTable');
			element.appendChild(table);

			/* Cabecera */
			line = document.createElement("TR");
			line.setAttribute('class', 'contentLineHeader');
			table.appendChild(line);

			td1 = document.createElement("TD");
			cellContent1 = document.createTextNode("Descripcion");
			td1.appendChild(cellContent1);
			line.appendChild(td1);

			tdPrecios = document.createElement("TD");
			tdPrecios.colSpan = 2;
			tablePrecios = document.createElement("TABLE");
			tablePrecios.style.width = "100%";
			tdPrecios.appendChild(tablePrecios);
			tr1Precios = document.createElement("TR");
			tablePrecios.appendChild(tr1Precios);
			tdPrecios.appendChild(tablePrecios);

			tdPrecio = document.createElement("TD");
			cellContentPrecio = document.createTextNode("Precio");
			tdPrecio.style.textAlign = "center";
			tdPrecio.colSpan = 2;
			tdPrecio.appendChild(cellContentPrecio);
			tr1Precios.appendChild(tdPrecio);

			tr2Precios = document.createElement("TR");
			tablePrecios.appendChild(tr2Precios);

			td2 = document.createElement("TD");
			cellContent2 = document.createTextNode("Max");
			td2.appendChild(cellContent2);
			tr2Precios.appendChild(td2);

			td3 = document.createElement("TD");
			cellContent3 = document.createTextNode("Min");
			td3.appendChild(cellContent3);
			tr2Precios.appendChild(td3);

			line.appendChild(tdPrecios);

			td4 = document.createElement("TD");
			cellContent4 = document.createTextNode("Zona");
			td4.appendChild(cellContent4);
			line.appendChild(td4);
		}

		line = document.createElement("TR");
		line.setAttribute('class', 'linecontent');
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
	element.appendChild(table);
}

function drawFilters(arr, element) {
	var select = document.getElementById("filterSelect");
	var elemento = "";
	for (i = 0; i < arr.length; i++) {
		var opcion = document.createElement("option");
		opcion.setAttribute("value", arr[i]);
		var texto = document.createTextNode(arr[i]);
		opcion.appendChild(texto);
		select.appendChild(opcion);
		elemento = arr[i].description;
	}
	element.appendChild(select);
};

/**
 * Lee las configuraciones de la pantalla y las graba en el localstorage.
 * Se graban cada vez que sale de la pantalla.
 */
function savePreferences() {
	localstorage = window.localStorage;
	productos = document.getElementsByName("checkboxProductos");
	var prods = {};
	var jsonProd = [];
	prods.productTypes = jsonProd;
	for (e = 0; e < productos.length; e++) {
		labelProd = document.getElementById("label" + productos[e].id);
		prods.productTypes.push({
			"id" : productos[e].value,
			"checked" : productos[e].checked
		});
	}
	localstorage.setItem("products", JSON.stringify(prods));

	mercados = document.getElementsByName("checkboxMercados");
	var markets = {};
	jsonMark = [];
	markets.markets = jsonMark;
	for (e = 0; e < mercados.length; e++) {
		labelMercado = document.getElementById("label" + mercados[e].id);
		markets.markets.push({
			"id" : mercados[e].value,
			"checked" : mercados[e].checked
		});
	}
	console.log(JSON.stringify(markets));
	localstorage.setItem("markets", JSON.stringify(markets));
}

/**
 * Carga las preferencias del localstorage y tilda las configuraciones
 * Cualquier error en la lectura de las variables del localstorage las borra
 * (una independiente de la otra) con el fin de evitar errores por cambios 
 * de version.
 */
function loadPreferences() {
	localstorage = window.localStorage;
	var markets = {};
	try {
		markets = JSON.parse(localstorage.getItem("markets"));
		if (markets != null) {
			var marketsList = markets.markets;
			for (i = 0; i < marketsList.length; i++) {
				market = marketsList[i];
				var pageConfiguration = document.getElementById("checkbox"
						+ market.id);
				if (pageConfiguration != null) {
					pageConfiguration.checked = (market.checked == true);
				}
			}
		}
	} catch (err) {
		localstorage.removeItem("markets");
	}

	var productTypes = {};
	try {
		productTypes = JSON.parse(localstorage.getItem("products"));
		if (productTypes != null) {
			var productTypesList = productTypes.productTypes;
			for (i = 0; i < productTypesList.length; i++) {
				product = productTypesList[i];
				var pageConfiguration = document.getElementById("checkbox"
						+ product.id);
				if (pageConfiguration != null) {
					pageConfiguration.checked = (product.checked == true);
				}
			}
		}
	} catch (err) {
		localstorage.removeItem("products");
	}
}
