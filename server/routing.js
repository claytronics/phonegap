// Copyright (c) 2012-13, Seth Copen Goldstein
// Not to be used without prior permission.
// For permission email: seth.c.goldstein@gmail.com

// define how to handle incoming requests that aren't just plain file server actions

var actions = {
    'login': User.login,
    'newuser': User.newuser,
    'record': Info.record,
    'getevents': Info.getEvents,
    'info': Info.getInfo,
    'getsome': Info.getsome,
    'f': Item.find
};

function doAction(pathname, response, cb)
{
    var args = pathname.split('/');
    args.splice(0,2);
    console.log('doAction: %j', args);
    if (args[0] in actions) {
        console.log('Found action %s: %j, %j', args[0], actions[args[0]], actions);
        actions[args[0]](response, args);
    } else {
        response.err(404, "no req handler for "+pathname);
    }
    cb(null);
}

// basic directory fetch
function doDir(pathname, response, cb)
{
    console.log('dodir: %s', pathname)
    // see if index.php or index.html exist
    FileServer.checkServerPath(pathname+"index.php", function(exists) {
	    if (exists) {
	        cb(pathname+"index.php");
	    }
	    else FileServer.checkServerPath(pathname+"index.html", function(exists) {
	        if (exists) {
		        cb(pathname+"index.html");
	        }
	        else {
		        response.err(404, pathname+" not found.");
		        cb(null);
	        }
	    });
    });
}

// basic file fetch
function doFile(pathname, response, cb)
{
    console.log('dofile: %s', pathname)
    FileServer.checkServerPath(pathname, function(exists) {
	    if (exists == 1) {
            // serve file
            FileServer.serve(pathname, response);
            cb(null);
        } else if (exists == 2) {
            // do as directory
            cb(pathname+"/");
        } else {
		    response.err(404, pathname+" not found.");
		    cb(null);
	    }
	});
}

// exports an array of path-regexps and functions to handle them
// Function signature is (string pathname, Responder response, function(string) cb)
module.exports = [
    {"^/a/": doAction},
    {"/$": doDir},
    {".": doFile}
];


// Local Variables:
// tab-width: 4
// indent-tabs-mode: nil
// End:
