// Copyright (c) 2012-13, Seth Copen Goldstein
// Not to be used without prior permission.
// For permission email: seth.c.goldstein@gmail.com

var PhoneEvent = require('./db/pevent.js');

PhoneEvent.create = function(uid, type, time, lati, longi, x, y, z)
{
    var pe = new PhoneEvent();
    pe.user = uid;
    pe.type = type;
    pe.date = new Date(time);
    pe.latitude = lati;
    pe.longitude = longi;
    pe.ax = x;
    pe.ay = y;
    pe.az = z;
    return pe;
};

module.exports = PhoneEvent;

// Local Variables:
// tab-width: 4
// indent-tabs-mode: nil
// End:
