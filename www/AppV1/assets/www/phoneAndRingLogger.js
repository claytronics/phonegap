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
          checkDataReady(2);
          phonenumber = number;
          });

       ringStateListener.start(function (state,number){
		  //alert("state"+state);
          checkDataReady(4);
          state = state;
          //alert("state in ring"+state);
          });
}
