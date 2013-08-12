function CreateTables()
{
		   db.transaction(function (tx) {
	         tx.executeSql('CREATE TABLE IF NOT EXISTS PEVENT(id unique, date, latitude, longitude, ax, ay, az, type)');
	         tx.executeSql('CREATE TABLE IF NOT EXISTS APPS(id unique, name, fg, importance, startTime, endTime, terminated)');
	         tx.executeSql('CREATE TABLE IF NOT EXISTS CALLS(id unique,phonenumber,ignored,starttime,endtime)');
	         tx.executeSql('CREATE TABLE IF NOT EXISTS MSGS(id unique,phonenumber,starttime,whenRead)');
	         tx.executeSql('CREATE TABLE IF NOT EXISTS RINGTONE(id unique,status)');
	       });
}

function InsertOnServer(type)
{
 var time = getTimeStamp();

  switch(type)
			{

      	    /*CALL */
      	    case 2:
      	       var uri = rootURI + 'record/' + uid + '/'+ '2/' + encodeURIComponent(time) + '/' + currLat.toString() + '/' + currLon.toString() + '/' + ax.toString() + '/' + ay.toString() + '/' + az.toString() + '/' + phoneNumber + '/' + time + '/' + null + '/' + null;
      	    break;

      	    /*MSG*/
      	    case 3:
      	       var uri = rootURI + 'record/' + uid + '/'+ '3/' + encodeURIComponent(time) + '/' + currLat.toString() + '/' + currLon.toString() + '/' + ax.toString() + '/' + ay.toString() + '/' + az.toString() + '/' + null;
      	    break;

      	    /*RINGER*/
      	    case 4:
      	       var uri = rootURI + 'record/' + uid + '/'+ '4/' + encodeURIComponent(time) + '/' + currLat.toString() + '/' + currLon.toString() + '/' + ax.toString() + '/' + ay.toString() + '/' + az.toString() + '/' + null;
      	    break;

      	    /* JUST PEVENT */
      	    default:
      	       var uri = rootURI + 'record/' + uid + '/' + '0/' + encodeURIComponent(time) + '/' + currLat + '/' + currLon + '/' + ax + '/' + ay + '/' + az;
      	    break;

      	    }

      	    //alert(uri);
      	    doRequest(uri,0);
}

function checkDataReady(type)
{
    //alert("checkDataReady");

    if(uid != null)
       InsertOnServer(type);


		// TBD: do a better way of sync; this is pathetic
		//if((currLat!=null) && (currLon!=null) && (ax != null) && (ay != null) && (az != null))
		//{
			// TBD: do some form of checking the timestamps on each param (accln, locn etc)
			// if they are close enough to curr time then send else (??)may be some kind of avg
			// if they are close to each other.

		//}

	   InsertDataBase(type);

}

function InsertAppData(time)
{
        if(AppData != null)
		{
			//alert('App logger success ' + AppData.length);
			for(var i = 0; i < Math.floor(AppData.length/11) * 11; i=i+11)
			{
				db.transaction(function (tx) {
		        tx.executeSql('SELECT * FROM PEVENT', [], function (tx, results) {
		         NoRows = results.rows.length;
		         if(NoRows =="undefined")
		         {
		           NoRows = 0;
		         }
		         NoRows = NoRows+1;
		         //alert("NoRows"+NoRows);
		         },null);
		        });

				db.transaction(function (tx) {
				//alert("Inserting App Accl Data " + AppData.join());
	      		tx.executeSql('INSERT INTO PEVENT(id, date, latitude, longitude, ax, ay, az, type) VALUES (?,?,?,?,?,?,?,?)',[NoRows, time, AppData[i+0], AppData[i+1],AppData[i+2],AppData[i+3],AppData[i+4],type]);
	         	});
	        }

         	db.transaction(function (tx) {
	        tx.executeSql('SELECT * FROM APPS', [], function (tx, results) {
	         NoRows = results.rows.length;
	         if(NoRows =="undefined")
	         {
	           NoRows = 0;
	         }
	         NoRows = NoRows+1;
	         //alert("Apps NoRows"+NoRows);
	         },null);
	        });

			db.transaction(function (tx) {
				for(var i = 0; i < Math.floor(AppData.length/11) * 11; i=i+11)
				{
					//alert("Inserting App Usage Data " + AppData.join());

					tx.executeSql('INSERT INTO APPS(id, name, fg, importance, startTime, endTime, terminated) VALUES (?,?,?,?,?,?,?)',[NoRows+(i/11),AppData[i+5],AppData[i+6],AppData[i+7],AppData[i+8],AppData[i+9],AppData[i+10]]);
					//alert('inserted into apps');
				}
				//alert("Making AppData = null");
				AppData = null;
			});

		}
}

function InsertPeventData(type,time)
{
   var NoRows = null;

   db.transaction(function (tx) {
	        tx.executeSql('SELECT * FROM PEVENT', [], function (tx, results) {
	         NoRows = results.rows.length;

	         if(NoRows =="undefined")
	              NoRows = 0;

	         NoRows = NoRows+1;
	           //alert("NoRows"+NoRows);
	         },null);
	        });

	      db.transaction(function (tx) {
	         tx.executeSql('INSERT INTO PEVENT(id, date, latitude, longitude, ax, ay, az, type) VALUES (?,?,?,?,?,?,?,?)',[NoRows, time, currLat,currLon,ax,ay,az,type]);
	           switch(type)
	           {
	             case 2:
	               tx.executeSql('INSERT INTO CALLS(id,phonenumber,ignored,starttime,endtime) VALUES (?,?,?,?,?)',[NoRows,phoneNumber,,time,]);
	             break;
	             case 3:
	               tx.executeSql('INSERT INTO MSGS(id,phonenumber,starttime,whenRead) VALUES (?,?,?,?)',[NoRows,,,]);
	             break;
	             case 4:
	               tx.executeSql('INSERT INTO RINGTONE(id,status) VALUES (?,?)',[NoRows,status]);
	              break;
	             default:
	             break;
	           }

	            aTime = null;
				lTime = null;
	     });
}

function InsertDataBase(type)
{
          var NoRows = null;
	      var time = getTimeStamp();

	       if(type == 1)
	          InsertAppData(time);
	       else
	          InsertPeventData(type,time);
}

function getTimeStamp()
{
       var now = new Date();
       return ((now.getMonth() + 1) + '/' + (now.getDate()) + '/' + now.getFullYear() + " " + now.getHours() + ':'
                     + ((now.getMinutes() < 10) ? ("0" + now.getMinutes()) : (now.getMinutes())) + ':' + ((now.getSeconds() < 10) ? ("0" + now
                     .getSeconds()) : (now.getSeconds())));
}