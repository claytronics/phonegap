var ShellCmds = {

	Top: function(successCallback, errorCallback, options) {
		// Default interval (10 sec)
        var frequency = (options && options.frequency && typeof options.frequency == 'number') ? options.frequency : 10000;
		//alert("Top");
		cordova.exec(successCallback, errorCallback, "ShellCmds", "Top", []);
    },
	
	PS: function(successCallback, errorCallback, options) {
		// Default interval (10 sec)
        var frequency = (options && options.frequency && typeof options.frequency == 'number') ? options.frequency : 10000;
	    //alert("PS");
		cordova.exec(successCallback, errorCallback, "ShellCmds", "PS", []);			                
    },
	
	Kill: function(successCallback, errorCallback, options) {
		cordova.exec(successCallback, errorCallback, "ShellCmds", "Kill", [options.pid]);
    }

};
