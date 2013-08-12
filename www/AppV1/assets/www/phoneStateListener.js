phoneStateListener = {
  RINGIGNG: 'RINGING',
  OFFHOOK: 'OFFHOOK',
  IDLE: 'IDLE',
  NONE: 'NONE',

  start: function(callback) {
    //alert("In startPhone");
    cordova.exec(function (data){
      callback(data["state"],data["number"]);
	  },function (){},'phoneStateListener','start',[]);
  },

  stop: function(callback) {
    //alert("In stop");
    cordova.exec(callback, function (){}, 'phoneStateListener', 'stop',[]);
  }
};

ringStateListener = {
  RINGIGNG: 'RINGING',
  OFFHOOK: 'OFFHOOK',
  IDLE: 'IDLE',
  NONE: 'NONE',

  start: function(callback) {
    //alert("In startPhone");
    cordova.exec(function (data){
      callback(data["state"],data["number"]);
	  },function (){},'ringStateListener','start',[]);
  },

  stop: function(callback) {
    //alert("In stop");
    cordova.exec(callback, function (){}, 'ringStateListener', 'stop',[]);
  }
};

