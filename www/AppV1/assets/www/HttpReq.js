var myxmlhttp;
//doRequest();

function doRequest(url,callReqd) 
{
	/*var url = "http://localhost/a/record/1/Thu%2C 18 Jul 2013 20%3A26%3A22 GMT/100.1/200.2/1.2/2.3/3.4/foo/true/203/Thu%2C 18 Jul 2013 20%3A20%3A22 GMT/Thu%2C 18 Jul 2013 20%3A22%3A22 GMT/false";*/
	
	myxmlhttp = CreateXmlHttpReq(resultHandler,callReqd);

	if (myxmlhttp) 
	{
		XmlHttpGET(myxmlhttp, url);
	} 
	else 
	{
		alert("An error occured while attempting to process your request.");
		return 0;
		// provide an alternative here that does not use XMLHttpRequest
	}
	
	return 1;
}

function resultHandler () 
{	
	// request is 'ready'
	if (myxmlhttp.readyState == 4) 
	{
		// success
		if (myxmlhttp.status == 200) 
		{
           //var abc = '{"status":0,"msg":"ok","data":{"id":"51f2405898ce6af013000001","name":"kk","email":"kk"}}';
           var cde = eval('('+myxmlhttp.responseText+')');
           data = JSON.stringify(cde["data"]);
		} 
		else 
		{
			alert("There was a problem retrieving the data:\n" + req.statusText);
		}
	}
}

function CreateXmlHttpReq(handler,callReqd) 
{
	var xmlhttp = null;

	if (window.XMLHttpRequest) 
	{
		xmlhttp = new XMLHttpRequest();
	} 
	else if (window.ActiveXObject) 
	{
		// users with activeX off
		try 
		{
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		} 
		catch (e) {}
	}

	if (xmlhttp && callReqd) xmlhttp.onreadystatechange = handler;

	return xmlhttp;
}

// XMLHttp send GEt request
function XmlHttpGET(xmlhttp, url) 
{
	try 
	{
		xmlhttp.open("GET", url, true);	
		xmlhttp.send(null);
	} 
	catch (e) {}
}


