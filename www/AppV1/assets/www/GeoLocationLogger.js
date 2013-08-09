	function GeolocationLogger()
	{
		// this func is called once every "interval"
		
		// get current location and put in local DB
		//alert('get locn');
		
		currLat = null;
		currLon = null;
		lTime = null;
		navigator.geolocation.getCurrentPosition(onSuccessLocn, onErrorLocn);
	}
	
	function onSuccessLocn(position)
	{
		// notify 
		//alert('got locn');
			
		currLat = position.coords.latitude;
		currLon = position.coords.longitude;
		lTime = position.timestamp;
	}
	
	function onErrorLocn()
	{
		alert("Could not get location!");
	}
	