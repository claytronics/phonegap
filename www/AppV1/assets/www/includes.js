
    var uid = null;
    var watchID = null;
	var AcclID = null;
	var LocnID = null;
	var AppListID = null;
	var phoneCallsId =  null;
	var DBpushID = null;
	var readyCheckID = null;
	var intervalID = null;

	var frequency = 10000;
	var rootURI = "http://128.237.207.145:80/a/";
    //var rootURI = "http://192.168.56.1:80/a/"
    var userName = null;
	var currLat = null;
	var currLon = null;
	var ax = null;
	var ay = null;
	var az = null;
	var aTime = null;
	var lTime = null;
	var AppData = null;
	var state = null;
	var phonenumber = null;
    var start = 0;
	var db = openDatabase('mydb', '1.0', 'Test DB', 2 * 1024 * 1024);
	var NoRows;
    var data = null;
    var fromLocAccDate = null;
    var toLocAccDate = null;
    var fromLocAppDate=null;
    var toLocAppDate=null;
    var fromLocMapDate=null;
    var toLocMapDate=null;
    var status = 0;


