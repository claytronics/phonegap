var appList = {

	Running: function(successCallback, errorCallback, options) {
		// Default interval (10 sec)
        var frequency = (options && options.frequency && typeof options.frequency == 'number') ? options.frequency : 10000;
	
		//timer:window.setInterval(function() {
                    cordova.exec(successCallback, errorCallback, "ShowAppList", "Running", []);                
            //}, frequency);		
    },

};
