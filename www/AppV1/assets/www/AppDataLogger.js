	function AppDataLogger()
	{
		// push data to local DB - this happens only when something significant happens
		// like FG<->BG switching, app termination, or user stops the tracking etc

		// 1. List of App Names
		// 2. FG/BG ie importance
		// 3. start/end times, terminated
		cordova.exec(OnSuccessApp, OnErrorApp, "AppTracker", "Running", []);
	}

	function OnSuccessApp(AppEvent)
	{
		// get current accln and locn data; append to App Event data
		if(AppEvent != null)
		{
			AppData = AppEvent.split('/');
			alert('App logger success ' + AppData.length);
			var now = new Date();
			for(var i = 0; i < Math.floor(AppData.length/6) * 6; i=i+6)
			{
				var uri = rootURI + '1/' + encodeURIComponent(now.toString()) + '/' + 'null' + '/' + 'null' + '/' + 'null' + '/' + 'null' + '/' + 'null' + '/' +
						  AppData[i+0] + '/' + AppData[i+1] + '/' + AppData[i+2] + '/' + encodeURIComponent(AppData[i+3]) + '/' + encodeURIComponent(AppData[i+4]) + '/' + AppData[i+5];
						  // Name                 FG?                 Imp                      Start				                      End                                Term?
				alert('sending app data ' + uri);
				doRequest(uri);
			}
		}
	}

	function OnErrorApp()
	{
		alert('App Data Logger Failed to get App Event!');
	}
