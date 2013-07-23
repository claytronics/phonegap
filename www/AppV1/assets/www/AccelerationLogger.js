	function AccelerationLogger()
	{
		// this func is called once every "interval"
		
		// 	get current location and put in local DB
		ax = null;
		ay = null;
		az = null;
		aTime = null;
		navigator.accelerometer.getCurrentAcceleration(onSuccessAccl, onErrorAccl);
	}
	
	function onSuccessAccl(acceleration)
	{
		// notify 
		//alert('got accln');			
		ax = acceleration.x;
		ay = acceleration.y;
		az = acceleration.z;
		aTime = acceleration.timestamp;
	}
	
	function onErrorAccl()
	{
		alert("Could not get acceleration!");
	}	
	