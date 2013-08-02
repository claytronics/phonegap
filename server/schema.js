{
    "copyright": "// Copyright (c) 2013, Seth Copen Goldstein.  All Rights Resevered.\n// Not to be used without prior permission.\n// For permission email: seth.c.goldstein@gmail.com\n\n",
    "schema": [
    {
	"class": "PhoneEvent",
	"table": "pevent",
	"backend": "db/pevent.js",
	"frontend": "../d0/sjs/db/pevent.js",
	"reaptime": 240,
	"fields": 
	[
	    {
		"type": "!DBID",
		"name": "user"
	    },
	    {
		"type": "!Date",
		"name": "date"
	    },
	    {
		"type": "!number",
		"name": "latitude"
	    },
	    {
		"type": "!number",
		"name": "longitude"
	    },
	    {
		"type": "!number",
		"name": "ax"
	    },
	    {
		"type": "!number",
		"name": "ay"
	    },
	    {
		"type": "!number",
		"name": "az"
	    },
	    {
		"type": "!number",
		"name": "type"
	    }
	]
    },
    {
	"class": "Application",
	"table": "apps",
	"backend": "db/apps.js",
	"frontend": "../d0/sjs/db/apps.js",
	"reaptime": 240,
	"fields": 
	[
	    {
		"type": "!string",
		"name": "name"
	    },
	    {
		"type": "!boolean",
		"name": "fg"
	    },
	    {
		"type": "!number",
		"name": "importance"
	    },
	    {
		"type": "!Date",
		"name": "startTime"
	    },
	    {
		"type": "!Date",
		"name": "endTime"
	    },
	    {
		"type": "!boolean",
		"name": "terminated"
	    }
	]
    },
    {
	"class": "Call",
	"table": "calls",
	"backend": "db/calls.js",
	"frontend": "../d0/sjs/db/calls.js",
	"reaptime": 240,
	"fields": 
	[
	    {
		"type": "!string",
		"name": "phoneNumber"
	    },
	    {
		"type": "!boolean",
		"name": "ignored"
	    },
	    {
		"type": "!Date",
		"name": "startTime"
	    },
	    {
		"type": "!Date",
		"name": "endTime"
	    }
	]
    },
    {
	"class": "Msg",
	"table": "msgs",
	"backend": "db/msgs.js",
	"frontend": "../d0/sjs/db/msgs.js",
	"reaptime": 240,
	"fields": 
	[
	    {
		"type": "!string",
		"name": "phoneNumber"
	    },
	    {
		"type": "!Date",
		"name": "startTime"
	    },
	    {
		"type": "?Date",
		"name": "whenRead"
	    }
	]
    },
    {
	"class": "RingTone",
	"table": "ringtone",
	"backend": "db/ringtone.js",
	"frontend": "../d0/sjs/db/ringtone.js",
	"reaptime": 240,
	"fields": 
	[
	    {
		"type": "!Date",
		"name": "startTime"
	    },
	    {
		"type": "!number",
		"name": "status"
	    }
	]
    },
    {
	"class": "User",
	"table": "user",
	"backend": "db/user.js",
	"frontend": "../d0/sjs/db/user.js",
	"reaptime": 240,
	"fields": 
	[
	    {
		"type": "!string",
		"name": "name"
	    },
	    {
		"type": "!string",
		"name": "email",
		"key": "unique"
	    }
	]
    }
]
}


