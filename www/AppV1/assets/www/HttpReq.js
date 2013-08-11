var myxmlhttp;

//doRequest();

function doRequest (url) {
	myxmlhttp = CreateXmlHttpReq(resultHandler);

	if (myxmlhttp) {
		XmlHttpGET(myxmlhttp, url);
	} else {
		alert("An error occured while attempting to process your request.");
		// provide an alternative here that does not use XMLHttpRequest
	}
}

function resultHandler () {
	// request is 'ready'
	if (myxmlhttp.readyState == 4) {
		// success
		if (myxmlhttp.status == 200) {
			//alert("Success!");
			// myxmlhttp.responseText is the content that was received from the request

			// Registration : in the registration process xmlRootNode return current status that user registered or not.		
			var responsedata = myxmlhttp.responseXML;
			var xmlRootNode = responsedata.getElementsByTagName("error")[0].firstChild.nodeValue;
			//alert(xmlRootNode);

			// Lgoin : after login on forum user get access token, this token used on login success page.
			var responsedata = myxmlhttp.responseXML;
			var xmlRootNode = responsedata.getElementsByTagName("authtoken")[0].firstChild.nodeValue;
			//alert(xmlRootNode);
			

		} else {
			alert("There was a problem retrieving the data:\n" + req.statusText);
		}
	}
}

function CreateXmlHttpReq(handler) {
	var xmlhttp = null;

	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		// users with activeX off
		try {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e) {}
	}

	if (xmlhttp) xmlhttp.onreadystatechange = handler;

	return xmlhttp;
}

// XMLHttp send GEt request
function XmlHttpGET(xmlhttp, url) {
	try {
		xmlhttp.open("GET", url, true);	

		xmlhttp.send(null);
	} catch (e) {}
}


