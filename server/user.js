// Copyright (c) 2012-13, Seth Copen Goldstein
// Not to be used without prior permission.
// For permission email: seth.c.goldstein@gmail.com

var User = require('./db/user.js');

/**
 * login
 * this logs user in. return null if no such user
 *
 * @param {!Responder} response
 * @param {!Array.<!string>} args
 **/
User.login = function(response, args)
{
    // args[1]: email
    var key = decodeURIComponent(args[1]);//email
    var success = function(user) {
        user.setCookie(response, user.id);
        response.returnJSON(user.forFrontEnd());
    };
    User.findByEmail(key, function(user) {
	    console.log('%s -> %j', key, user);
	    if (user) {
            success(user);
            return;
	    }
        response.returnJSON(null);
    });
};

module.exports = User;

// Local Variables:
// tab-width: 4
// indent-tabs-mode: nil
// End:
