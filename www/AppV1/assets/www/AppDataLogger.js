	function AppDataLogger(frequency)
	{
		// push data to local DB - this happens only when something significant happens
		// like FG<->BG switching, app termination, or user stops the tracking etc

		// 1. List of App Names
		// 2. FG/BG ie importance
		// 3. start/end times, terminated
		//alert('app logger');
		cordova.exec(OnSuccessApp, OnErrorApp, "AppTracker", "Running", []);
	}

	function OnSuccessApp(AppEvent)
	{
		// get current accln and locn data; append to App Event data
		//alert('applogger successCB');
		if(AppEvent != null)
		{
			AppData = AppEvent.split('|');
			//alert('App logger success ' + AppData.length);
			for(var i = 0; i < Math.floor(AppData.length/11) * 11; i=i+11)
			{
				var uri = rootURI + 'record/' + uid + '/' + '1/' + encodeURIComponent(getTimeStamp()) + '/' 
				+ AppData[i+0] + '/' + AppData[i+1] + '/' + AppData[i+2] + '/' + AppData[i+3] + '/' + AppData[i+4] + '/' 
				+ AppData[i+5] + '/' + AppData[i+6] + '/' + AppData[i+7] + '/' + encodeURIComponent(AppData[i+8]) + '/' + encodeURIComponent(AppData[i+9]) + '/' + AppData[i+10];
					// lat               lon	                ax                  ay                    az                          
					// Name              FG?                   Imp                  Start                                    End                                    Term?
				//alert('sending app data ' + uri);
				doRequest(uri);				
			}
			
			//alert('Calling InsertDB');
			InsertDataBase(1);
		}
	}

	function OnErrorApp()
	{
		alert('App Data Logger Failed to get App Event!');
	}
