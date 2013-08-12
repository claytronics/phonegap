var data = null;

/* Asynchornous request to server.
       Can't synchoronize and block code execution.
       Can't just let the code pass without getting data.
       So,the below function refresh */

    function refresh()
    {
      if(data!=null && data!='undefined')
      {
	    var def = eval('('+data+')');
        uid = def["id"];
         //alert("uid in index"+uid);
        data = null;
        clearInterval(intervalID);
      }
    }

	function getUID()
	{
	   var uri = rootURI + 'login/kk';
	   //alert("URI"+uri);

	   if(doRequest(uri,1))
	   {
	      intervalID = setInterval(refresh, 1000);
	   }
	   else
	   {
	      alert("Could not connect to server");
	   }
	}
