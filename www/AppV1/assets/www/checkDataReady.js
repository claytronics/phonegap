function CreateTables()
{
		   db.transaction(function (tx) {
	         tx.executeSql('CREATE TABLE IF NOT EXISTS PEVENT(id unique, date DATETIME, latitude, longitude, ax, ay, az, type)');
	         tx.executeSql('CREATE TABLE IF NOT EXISTS APPS(id unique, name, fg, importance, startTime DATETIME, endTime DATETIME, terminated)');
	         tx.executeSql('CREATE TABLE IF NOT EXISTS CALLS(id unique,phonenumber,ignored,starttime DATETIME,endtime DATETIME)');
	         tx.executeSql('CREATE TABLE IF NOT EXISTS MSGS(id unique,phonenumber,starttime DATETIME,whenRead)');
	         tx.executeSql('CREATE TABLE IF NOT EXISTS RINGTONE(id unique,status)');
	       });
}

function checkConnection() {
        var networkState = navigator.connection.type;

        var states = {};
        states[Connection.UNKNOWN]  = 'Unknown connection';
        states[Connection.ETHERNET] = 'Ethernet connection';
        states[Connection.WIFI]     = 'WiFi connection';
        states[Connection.CELL_2G]  = 'Cell 2G connection';
        states[Connection.CELL_3G]  = 'Cell 3G connection';
        states[Connection.CELL_4G]  = 'Cell 4G connection';
        states[Connection.CELL]     = 'Cell generic connection';
        states[Connection.NONE]     = 'No network connection';

		if((networkState == Connection.WIFI) || (networkState == Connection.CELL_4G) || (networkState == Connection.CELL_3G))
		{
			return 1;
		}
		else
			return 0;
        //alert('Connection type: ' + states[networkState]);
}

function InsertOnServer()
{
	
	/* retrieve from local DB and push to remote DB */
	
	//alert("In InsertOnServer " + pEventsDone + ' ' + appsDone + ' ' + callsDone + ' ' + msgsDone + ' ' +  ringerDone);
	
	if((pEventsDone == 1) && (appsDone == 1) && (callsDone == 1) && (msgsDone == 1) && (ringerDone == 1))	
	{
		//alert("resetting flags");
		pEventsDone = 0;
		appsDone = 0;
		callsDone = 0;
		msgsDone = 0;
		ringerDone = 0;
		pEventResults = null;
	}
	
	/* syncing with pevent table as reference point */
	if((pEventResults == null) && (uid != null))
	{	
		if(peventNumRows == null)
		{
			peventNumRows = new Array();
			peventNumRows[0] = 0;
		}
		
		db.transaction(
		function (tx) 
		{
			//alert("peventNumRows " + peventNumRows.join());
			
			var qArray = "";
			for (var i=0;i<peventNumRows.length;i++)
				qArray += (qArray == "" ? "" : ", ") + "?";
				
			//alert('Pevent ' + qArray + ' ' + peventNumRows.join());
			
			tx.executeSql('SELECT * FROM PEVENT WHERE id NOT IN (' + qArray + ')', peventNumRows, 
				function (tx, results)
				{
					//alert('pEventResults ' + results.rows.length);
					
					pEventResults = results;
					for(var i=0;i<results.rows.length;i++)
					{ 
						var nw = checkConnection();
						if(nw == 1)
						{
							if(results.rows.item(i).type == 0)
							{
								/* only pevent - just accln n locn */
								//alert("In InsertOnServer case pevent " + results.rows.item(i).id + ' ' + results.rows.item(i).type);

								var uri = rootURI + 'record/' + uid + '/' + (results.rows.item(i).type) + '/' + encodeURIComponent(results.rows.item(i).date) + '/' 
								+ results.rows.item(i).latitude + '/' + results.rows.item(i).longitude + '/' + results.rows.item(i).ax + '/' + results.rows.item(i).ay + '/' + results.rows.item(i).az; 
									// lat               lon	                ax                  ay                    az                          
								doRequest(uri, 0);
								
								peventNumRows.push(results.rows.item(i).id);
							}
						}	
					}
					//alert("pEventsDone = 1");
					pEventsDone = 1;
					
				}
			, null);
		}
		);	// db transacn pevent

		/* append apps table data */
		//if(pEventResults != null)
		db.transaction(
		function (tx) 
		{
			var pEventResultIds = new Array();
			var pEventIndex = new Array();
			var j =0;
			
			for(var i=0;i<pEventResults.rows.length;i++)
			{
				if(pEventResults.rows.item(i).type == 1)
				{
					pEventResultIds[j] = pEventResults.rows.item(i).id;
					pEventIndex[j] = i;
					j++;
				}
			}
			
			var qArray = "";
			for (var i=0;i<pEventResultIds.length;i++)
				qArray += (qArray == "" ? "" : ", ") + "?";
				
			//alert('Apps ' + qArray + ' ' + pEventResultIds.join());
			
			var nw = checkConnection();
			if(nw == 1)
			{
				/* append apps table data */
				//alert("In InsertOnServer case apps " + pEventResultIds.length);
				tx.executeSql('SELECT * FROM APPS WHERE id IN (' + qArray + ')', pEventResultIds, 
					function (tx, appres)
					{
						for(var j=0;j<appres.rows.length;j++)  
						{
							//alert("Apps event insert to server " + appres.rows.length);
							var uri = rootURI + 'record/' + uid + '/1/' + encodeURIComponent(pEventResults.rows.item(pEventIndex[j]).date) + '/' 
							+ pEventResults.rows.item(pEventIndex[j]).latitude + '/' + pEventResults.rows.item(pEventIndex[j]).longitude + '/' + pEventResults.rows.item(pEventIndex[j]).ax + '/' + pEventResults.rows.item(pEventIndex[j]).ay + '/' + pEventResults.rows.item(pEventIndex[j]).az + '/' 
							+ appres.rows.item(j).name + '/' + appres.rows.item(j).fg + '/' 
							+ appres.rows.item(j).importance + '/' + encodeURIComponent(appres.rows.item(j).startTime) + '/' 
							+ encodeURIComponent(appres.rows.item(j).endTime) + '/' + appres.rows.item(j).terminated;
							
							doRequest(uri, 0);
				
							peventNumRows.push(pEventResults.rows.item(pEventIndex[j]).id);									
						}
						//alert("appsDone = 1");
						appsDone = 1;
					}
				, null);
				
					
			}
		}		
		); // db transacn apps
		
		/* append calls table data */
		//if(pEventResults != null)
		db.transaction(
		function (tx) 
		{
			var pEventResultIds = new Array();
			var pEventIndex = new Array();
			var qArray = "";
			var j =0;
			
			for(var i=0;i<pEventResults.rows.length;i++)
			{
				if(pEventResults.rows.item(i).type == 2)
				{
					pEventResultIds[j] = pEventResults.rows.item(i).id;
					pEventIndex[j] = i;
					j++;
				}
			}
			
			for(var i=0;i<pEventResultIds.length;i++)
				qArray += (qArray == "" ? "" : ", ") + "?";
				
			//alert('Calls ' + qArray + ' ' + pEventResultIds.join());
			
			var nw = checkConnection();
			if(nw == 1)
			{
				/* append calls table data */
				//alert("In InsertOnServer case calls " + pEventResultIds.length);

				tx.executeSql('SELECT * FROM CALLS WHERE id IN ('+ qArray +')', pEventResultIds, 
					function (tx, callres)
					{
						// not necessary to loop for length:
						// there should be only one entry in CALLS table with this id
						for(var j=0;j<callres.rows.length;j++)  
						{
							//alert("Calls event insert to server " + callres.rows.length);
							var uri = rootURI + 'record/' + uid + '/' + pEventResults.rows.item(pEventIndex[j]).type + '/' + encodeURIComponent(pEventResults.rows.item(pEventIndex[j]).date) + '/' 
							+ pEventResults.rows.item(pEventIndex[j]).latitude + '/' + pEventResults.rows.item(pEventIndex[j]).longitude + '/' + pEventResults.rows.item(pEventIndex[j]).ax + '/' + pEventResults.rows.item(pEventIndex[j]).ay + '/' + pEventResults.rows.item(pEventIndex[j]).az + '/' 
							+ callres.rows.item(j).phonenumber + '/' + callres.rows.item(j).ignored + '/' 
							+ encodeURIComponent(callres.rows.item(j).starttime) + '/' 
							+ encodeURIComponent(callres.rows.item(j).endtime);
							
							doRequest(uri, 0);
				
							peventNumRows.push(pEventResults.rows.item(pEventIndex[j]).id);									
							
						}
						//alert("callsDone = 1");
						callsDone = 1;							
					}
				, null);
			}	
			
			
		}
		); // db transacn calls

		/* append msgs table data */
		//if(pEventResults != null)
		db.transaction(
		function (tx) 
		{
			var pEventResultIds = new Array();
			var pEventIndex = new Array();
			var qArray = "";
			var j =0;
			
			for(var i=0;i<pEventResults.rows.length;i++)
			{
				if(pEventResults.rows.item(i).type == 3)
				{
					pEventResultIds[j] = pEventResults.rows.item(i).id;
					pEventIndex[j] = i;
					j++;
				}
			}
			
			for(var i=0;i<pEventResultIds.length;i++)
				qArray += (qArray == "" ? "" : ", ") + "?";
				
			//alert('Msgs ' + qArray + ' ' + pEventResultIds.join());
			
			var nw = checkConnection();
			if(nw == 1)
			{
				/* append msgs table data */
				//alert("In InsertOnServer case msgs " + pEventResultIds.length);

				tx.executeSql('SELECT * FROM MSGS WHERE id IN ('+ qArray +')', pEventResultIds, 
					function (tx, msgres)
					{
						// not necessary to loop for length:
						// there should be only one entry in CALLS table with this id
						for(var j=0;j<msgres.rows.length;j++)  
						{
							//alert("Msgs event insert to server " + msgres.rows.length);
							var uri = rootURI + 'record/' + uid + '/' + pEventResults.rows.item(pEventIndex[j]).type + '/' + encodeURIComponent(pEventResults.rows.item(pEventIndex[j]).date) + '/' 
							+ pEventResults.rows.item(pEventIndex[j]).latitude + '/' + pEventResults.rows.item(pEventIndex[j]).longitude + '/' + pEventResults.rows.item(pEventIndex[j]).ax + '/' + pEventResults.rows.item(pEventIndex[j]).ay + '/' + pEventResults.rows.item(pEventIndex[j]).az + '/' 
							+ msgres.rows.item(j).phonenumber + '/'
							+ encodeURIComponent(msgres.rows.item(j).starttime) + '/' 
							+ encodeURIComponent(msgres.rows.item(j).whenRead);
							
							doRequest(uri, 0);
				
							peventNumRows.push(pEventResults.rows.item(pEventIndex[j]).id);									
							
						}
						//alert("msgsDone = 1");
						msgsDone = 1;							
					}
				, null);
			}	
			
			
		}
		); // db transacn msgs

		
		/* append ringer table data */
		//if(pEventResults != null)
		db.transaction(
		function (tx) 
		{
			var pEventResultIds = new Array();
			var pEventIndex = new Array();
			var qArray = "";
			var j =0;
			
			for(var i=0;i<pEventResults.rows.length;i++)
			{
				if(pEventResults.rows.item(i).type == 4)
				{
					pEventResultIds[j] = pEventResults.rows.item(i).id;
					pEventIndex[j] = i;
					j++;
				}
			}
			
			for(var i=0;i<pEventResultIds.length;i++)
				qArray += (qArray == "" ? "" : ", ") + "?";
				
			//alert('Ringer ' + qArray + ' ' + pEventResultIds.join());
			
			var nw = checkConnection();
			if(nw == 1)
			{
				/* append ringer table data */
				//alert("In InsertOnServer case ringer " + pEventResultIds.length);
				tx.executeSql('SELECT * FROM RINGTONE WHERE id IN ('+ qArray +')', pEventResultIds, 
					function (tx, ringerres)
					{
						// not necessary to loop for length:
						// there should be only one entry in CALLS table with this id
						for(var j=0;j<ringerres.rows.length;j++)  
						{
							//alert("RINGTONE event insert to server " + ringerres.rows.length);
							var uri = rootURI + 'record/' + uid + '/' + pEventResults.rows.item(pEventIndex[j]).type + '/' + encodeURIComponent(pEventResults.rows.item(pEventIndex[j]).date) + '/' 
							+ pEventResults.rows.item(pEventIndex[j]).latitude + '/' + pEventResults.rows.item(pEventIndex[j]).longitude + '/' + pEventResults.rows.item(pEventIndex[j]).ax + '/' + pEventResults.rows.item(pEventIndex[j]).ay + '/' + pEventResults.rows.item(pEventIndex[j]).az + '/' 
							+ ringerres.rows.item(j).status;
							
							doRequest(uri, 0);
				
							peventNumRows.push(pEventResults.rows.item(pEventIndex[j]).id);									
							
						}
						//alert("ringerDone = 1");
						ringerDone = 1;							
					}
				, null);
			}	
			
			
		}
		); // db transacn ringer
	} //if peventrow == null

 }

function checkDataReady(type)
{      
	   InsertDataBase(type);
}

function InsertAppData(time)
{
        if(AppData != null)
		{
			db.transaction(function (tx) 
			{
				//alert('App logger success InsertAppData ' + AppData.length);				
				
				tx.executeSql('SELECT * FROM PEVENT', [], function (tx, results) 
				{
					NoRows = results.rows.length;
					for(var i = 0; i < Math.floor(AppData.length/11) * 11; i=i+11)
					{
						if(NoRows =="undefined")
						{
							NoRows = 0;
						}
					 	NoRows = NoRows+1;
					 	//alert("APPS NoRows "+NoRows);
					 	
					 	//alert("Inserting App Accl Data in PEVENT " + AppData.join() + ' ' + i);
					 	tx.executeSql('INSERT INTO PEVENT(id, date, latitude, longitude, ax, ay, az, type) VALUES (?,?,?,?,?,?,?,?)',[NoRows, time, AppData[i+0], AppData[i+1],AppData[i+2],AppData[i+3],AppData[i+4],1]);
					 	
					 	//alert("Inserting App Accl Data in APPS " + AppData.join() + ' ' + i);
					 	tx.executeSql('INSERT INTO APPS(id, name, fg, importance, startTime, endTime, terminated) VALUES (?,?,?,?,?,?,?)',[NoRows,AppData[i+5],AppData[i+6],AppData[i+7],AppData[i+8],AppData[i+9],AppData[i+10]]);
					 	
					 	if(i >= (Math.floor(AppData.length/11) - 1) * 11)
						{
							//alert("making appdata = null");
							AppData = null;
							break;
						}
					}// for
	        	},null); 
	        }); //db.transacn			
		} // if (AppData)
}

function InsertPeventData(type,time)
{
    var NoRows = null;

   if((currLat!=null) && (currLon!=null) && (ax != null) && (ay != null) && (az != null))
   {
      db.transaction(function (tx) 
      {
	        tx.executeSql('SELECT * FROM PEVENT', [], function (tx, results) 
	        {
	           NoRows = results.rows.length;

	           if(NoRows =="undefined")
	               NoRows = 0;

	           NoRows = NoRows+1;
	           //alert("NoRows"+NoRows);
	         },null);
	 });

	 db.transaction(function (tx) 
	 {
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