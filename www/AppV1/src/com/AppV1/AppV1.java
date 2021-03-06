/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.AppV1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.apache.cordova.*;

import com.AppV1.AppService;

public class AppV1 extends DroidGap
{
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Set by <content src="index.html" /> in config.xml
	// tried this, but it didn't seem to work
        super.setBooleanProperty("keepRunning", true);
        
	/////////// start of added code
	//
	// please start the service AppService (described in the .xml file in the root directory)
        Intent i=new Intent(this, AppService.class);
        
	// parameters to the service
        i.putExtra(AppService.EXTRA_PLAYLIST, "main");
        i.putExtra(AppService.EXTRA_SHUFFLE, true);
        
	// do the actual start
        startService(i);
	//
	/////////// End of added code
        
        super.loadUrl(Config.getStartUrl());
        //super.loadUrl("file:///android_asset/www/merge.html");
    }
	
	@Override
    public void onDestroy()
    {        
        stopService(new Intent(this, AppService.class));
        super.onDestroy();
    }
     
}

