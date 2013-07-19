var Application = require("./db/apps.js");
var Call = require("./db/calls.js");
var Msg = require("./db/msgs.js");
var RingTone = require("./db/ringtone.js");


Application.create = function(evid, name, fg, importance, startTime, endTime, terminated)
{
    var obj = new Application();
    obj._id = evid;
    obj.name = name;
    obj.fg = fg;
    obj.importance = importance;
    obj.startTime = startTime;
    obj.endTime = endTime;
    obj.terminated = terminated;
    return obj;
};


Call.create = function(evid, pnumber, starttime, endtime, ignored)
{
    var obj = new Call();
    obj._id = evid;
    obj.pnumber = pnumber;
    obj.starttime = starttime;
    obj.endtime = endtime;
    obj.ignored = ignored;
    return obj;
};

Msg.create = function(evid, pnumber, starttime, whenread)
{
    var obj = new Msg();
    obj._id = evid;
    obj.pnumber = pnumber;
    obj.starttime = starttime;
    obj.whenread = whenread;
    return obj;
};

RingTone.create = function(evid, status)
{
    var obj = new RingTone();
    obj._id = evid;
    obj.status = status;
    return obj;
};

module.exports = [ Application, Call, Msg, RingTone ];

// Local Variables:
// tab-width: 4
// indent-tabs-mode: nil
// End:
