function onPause()
{
       if(start == 1)
       PhoneCallsLogger();
       //alert("AFTER PHONE CALLS");
}

function PhoneCallsLogger()
{
	   //alert("PHONE CALLS LOGGER");
		phoneStateListener.start(function (state,number){
		 //alert("STATE in phone"+state);
		 //alert("number in phone"+number);
          phonenumber = number;
          var time=getTimeStamp();
          var ur2 = rootURI + 'record/' + uid + '/'+ '2/' + encodeURIComponent(time) + '/' + currLat + '/' + currLon + '/' + ax + '/' + ay + '/' + az + '/' + phonenumber + '/' + encodeURIComponent(time) + '/' + null + '/' + 'false';
		  //alert(ur2);
      	  doRequest(ur2,0);
          phoneNumber = null;
          //checkDataReady(2);
          });

       ringStateListener.start(function (states,number){
          state=number;
          //alert("state"+state);
          checkDataReady(4);

         });
}
