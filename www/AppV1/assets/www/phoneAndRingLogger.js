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
		   //alert("STATE"+state);
		   //alert("number"+number);
          checkDataReady(2);
          phonenumber = number;
          });

       ringStateListener.start(function (state,number){
		  //alert("state"+state);
          checkDataReady(4);
          state = state;
          });
}
