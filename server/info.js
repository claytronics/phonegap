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

/**
 * record
 * record an action
 *
 * @param {!Responder} response
 * @param {!Array.<!string>} args
 *
 * URL/a/record/type/time/lat/long/x/y/z/[string/bool/imp/start/end/term]
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

    var type = parseInt(args[1], 10);
    var ev = PhoneEvent.create(type, args[2], args[3], args[4], args[5], args[6], args[7]);
    console.log('Creating event %j', ev);
    ev.insert(function(dev) {
	var obj;
	switch (type) {
	case 1: obj = Application.create(dev._id, args[8], parseBool(args[9]), args[10], args[11], args[12], parseBool(args[13]));
	    break;
	case 2: obj = Call.create(dev._id, args[8], args[9], args[10], args[11], parseInt(args[12], 0));
	    break;
	case 3: obj = Msg.create(dev._id, args[8], args[9], args[10]);
	    break;
	case 4: obj = RingTone.create(dev._id, parseInt(args[8], 0));
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

module.exports = Info;