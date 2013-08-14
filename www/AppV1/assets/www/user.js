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
        data = null;
        clearInterval(intervalID);
      }
    }

    function createUID()
	{
	   var uri = rootURI + 'newuser/' + userName; 
	   doRequest(uri,0);
	}
    
	function getUID()
	{
	   var uri = rootURI + 'login/' + userName;
 
	   if(doRequest(uri,1))
	   {
	      intervalID = setInterval(refresh, 1000);
	   }
	}
