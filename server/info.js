// Copyright (c) 2012-13, Seth Copen Goldstein
// Not to be used without prior permission.
// For permission email: seth.c.goldstein@gmail.com

/**
 * Info
 *
 * @constructor
 *
**/
function Info()
{
}

function parseBool(x)
{
    if (x == "true") return true;
    else if (x == "false") return false;
    throw new Error("Bool not true or false");
}

Info.type = {
    None: 0,
    App: 1,
    Call: 2,
    Msg: 3,
    Ringer: 4,
    All: 5
};

Info.recordOffset = {
    UID: 1,
    Type: 2,
    Time: 3,
    Lat: 4,
    Long: 5,
    Ax: 6,
    Ay: 7,
    Az: 8,

    Name: 9,
    Fg: 10,
    Imp: 11,
    AppStartTime: 12,
    AppEndTime: 13,
    Term: 14,

    CallNum: 9,
    CallStartTime: 10,
    CallEndTime: 11,
    CallIgnored: 12,

    MsgNum: 9,
    MsgTime: 10,
    MsgReadTime: 11,

    RingTone: 9
};



/**
 * record
 * record an action
 *
 * @param {!Responder} response
 * @param {!Array.<!string>} args
 *
 * URL/a/record/uid/type/time/lat/long/x/y/z/[string/bool/imp/start/end/term]
 *				         [number/start/end/ignored]
 *				         [number/start/when]
 *				         [status]
 *
 **/
Info.record = function(response, args)
{
    for (var i=0; i<args.length; i++) {
	args[i] = decodeURIComponent(args[i]);
    }

    var uid = args[Info.recordOffset.UID];
    var type = parseInt(args[Info.recordOffset.Type], 10);
    var ev = PhoneEvent.create(uid, type, args[Info.recordOffset.Time], 
			       args[Info.recordOffset.Lat], args[Info.recordOffset.Long], 
			       args[Info.recordOffset.Ax], args[Info.recordOffset.Ay], args[Info.recordOffset.Az]);
    console.log('Creating event %j', ev);
    ev.insert(function(dev) {
	var obj;
	switch (type) {
	case Info.type.None:
	    // just loc info
	    response.returnJSON({id: dev._id});
	    return;

	case Info.type.App: obj = Application.create(dev._id, args[Info.recordOffset.Name], 
					 parseBool(args[Info.recordOffset.Fg]), 
					 parseInt(args[Info.recordOffset.Imp], 10), 
					 args[Info.recordOffset.AppStartTime], args[Info.recordOffset.AppEndTime], 
					 parseBool(args[Info.recordOffset.Term]));
	    break;
	case Info.type.Call: obj = Call.create(dev._id, args[Info.recordOffset.CallNum], 
				  args[Info.recordOffset.CallStartTime], args[Info.recordOffset.CallEndTime], 
				  parseBool(args[Info.recordOffset.CallIgnored]));
	    break;

	case Info.type.Msg: obj = Msg.create(dev._id, args[Info.recordOffset.MsgNum], args[Info.recordOffset.MsgTime], args[Info.recordOffset.MsgReadTime]);
	    break;

	case Info.type.Ringer: obj = RingTone.create(dev._id, parseInt(args[Info.recordOffset.RingTone], 0));
	    break;
	default:
	    throw new Error('Unknown type:'+type);
	}
	console.log('Creating %j', obj);
	obj.insert(function(x) {
	    response.returnJSON({id: x._id});
	});
    });
};


/**
 * record
 * record an action
 *
 * @param {!Responder} response
 * @param {!Array.<!string>} args
 *
 * URL/a/getevents/uid/type/start/end
 *
 **/
Info.getEvents = function(response, args)
{
    for (var i=0; i<args.length; i++) {
	args[i] = decodeURIComponent(args[i]);
    }
    var st = null;
    var et = null;
    if (args.length == 5) et = new Date(args[4]);
    if (args.length >= 4) st = new Date(args[3]);
    var uid = args[1];
    var type = parseInt(args[2], 10);
    console.log("Searching for %s events for %s from %s to %s", type, uid, st, et);
    var query = {user: uid};
    if (type != Info.type.All) query.type = type;
    if (st != null) query.date = {$gte: st};
    if (et != null) query.date = {$lte: et};
    db.advancedQuery(PhoneEvent.table, query, {_id:1}, {}, function(list) {
	response.returnJSON(list);
    });
};


/**
 * getInfo
 * get all info for a record (or records)
 *
 * @param {!Responder} response
 * @param {!Array.<!string>} args
 *
 * URL/a/getevents/uid/id [/id ...]
 *
 **/
Info.getInfo = function(response, args)
{
    var list = [];
    var synch = new Synchronizer(function () { 
	response.returnJSON(list);
    }, args.length-1, "gi");
    for (var i=1; i<args.length; i++) {
	id = decodeURIComponent(args[i]);
	PhoneEvent.find(id, function(obj) {
	    var co = obj.forFrontEnd(obj);
	    var addto = function(so) {
		var sobj = so.forFrontEnd(so);
		for (x in sobj) {
		    co.x = sobj.x;
		}
		list.push(co);
		synch.done(1);
	    };
	    switch (obj.type) {
	    case Info.type.None:
		list.push(co);
		synch.done(1);
		break;

	    case Info.type.App:
		Application.find(id, addto);
		break;

	    case Info.type.Call:
		Call.find(id, addto);
		break;

	    case Info.type.Msg:
		Msg.find(id, addto);
		break;

	    case Info.type.Ringer		:
		RingTone.find(id, addto);
		break;
	    default:
		throw new Error("Unknown type in info call for "+id);
	    }
	});
    }
};
    


module.exports = Info;