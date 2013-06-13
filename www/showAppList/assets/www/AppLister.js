var appList = {

	Running: function(successCallback, errorCallback) {
		//alert("appList.Running");
		cordova.exec(successCallback, errorCallback, "AppLister", "Running", []);
    },

};
