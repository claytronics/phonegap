phoneStateListener = {
  RINGIGNG: 'RINGING',
  OFFHOOK: 'OFFHOOK',
  IDLE: 'IDLE',
  NONE: 'NONE',
  start: function(callback) {
    alert("In start");
    cordova.exec(function (data){
      callback(data["state"],data["number"]);
	  },function (){},'phoneStateListener','start',[]);
  },
  stop: function(callback) {
    alert("In stop");
    cordova.exec(callback, function (){}, 'phoneStateListener', 'stop',[]);
  }
};


