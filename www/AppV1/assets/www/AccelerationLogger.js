	function AccelerationLogger(frequency)
	{
		// this func is called once every "interval"

		// 	get current location and put in local DB
		//alert('get accln');

		aTime = null;
		shake.startWatch(frequency);
		//alert('done get accln');
	}

	var shake = (function () {
	var shake = {},
		watchId = null,
		count = 0;
		AvgX=0;
		AvgY=0;
		AvgZ=0;
		frequency=300;
		options = { frequency: 300 },
		previousAcceleration = { x: null, y: null, z: null },
		shakeCallBack = null;

	// Start watching the accelerometer for a shake gesture
	shake.startWatch = function (freq) {
		//alert("startWatch");
		shake.frequency = freq/20;
		watchId = navigator.accelerometer.watchAcceleration(assessCurrentAcceleration, handleError, options);
	};

	// Stop watching the accelerometer for a shake gesture
	shake.stopWatch = function () {
		if (watchId !== null) {
			navigator.accelerometer.clearWatch(watchId);
			watchId = null;
		}
	};

	// Assess the current acceleration parameters to determine a shake
	function assessCurrentAcceleration(acceleration) {
	//alert("assessCurrentAcceleration");

		var accelerationChange = {x:0, y:0, z:0};
		if (previousAcceleration.x != null) {
			accelerationChange.x = Math.abs(previousAcceleration.x- acceleration.x);
			accelerationChange.y = Math.abs(previousAcceleration.y- acceleration.y);
			accelerationChange.z = Math.abs(previousAcceleration.z- acceleration.z);
				shake.stopWatch();
			if(count < 10)
			{
				//alert(count);

				AvgX = AvgX + accelerationChange.x;
				AvgY = AvgY + accelerationChange.y;
				AvgZ = AvgZ + accelerationChange.z;

				count++;
				setTimeout(shake.startWatch, shake.frequency, shakeCallBack);
			}
			else
			{
				count = 0;
				ax = AvgX/10;
				ay = AvgY/10;
				az = AvgZ/10;
				AvgX=0;
				AvgY=0;
				AvgZ=0;
				previousAcceleration = { x: null, y: null, z: null }
				//alert("Acceleration Logger " + ax + " " + ay + " " + az);
				shake.stopWatch();
			}
		}
		else {
			previousAcceleration = {
				x: acceleration.x,
				y: acceleration.y,
				z: acceleration.z
			}
		}
	}

	// Handle errors here
	function handleError() {
	}
	return shake;
	})();
