package org.apache.cordova.plugin;


import org.apache.cordova.api.CallbackContext;
import com.stericson.RootTools.*;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.CharSequence;

/**
 * This class echoes a string called from JavaScript.
 */
public class AppLister extends CordovaPlugin {
	private static final String TAG = "AppListerJava";
	private static int value =1;
	BroadcastReceiver receiver;
	private static HashMap<String,Integer> processListNew = null;
	private static HashMap<String,Integer> processListOld = null;
	Boolean alreadyPresent = false;
	Boolean changed = false;
	String ground = "";

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.w(TAG, "Start");

		processListNew = new HashMap<String,Integer>();

		if (action.equals("Running"))
		{
			Log.w(TAG, "1");
			ActivityManager am = (ActivityManager)cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			Log.w(TAG, "2");
			List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
			Log.w(TAG, "3");
			Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
			Log.w(TAG, "4");
			PackageManager pm = this.cordova.getActivity().getPackageManager();
			String AppList = "<br>";

			while(i.hasNext())
			{
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
				try
				{
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					Log.w("LABEL", c.toString());
					boolean AppCategory = (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).flags & ApplicationInfo.FLAG_SYSTEM)!=0;
					String AppType = AppCategory?"SYSTEM":"USER";
					AppList += (AppCategory?"<font color=\"red\">":"<font color=\"blue\">")+ c.toString() + " :" + AppType + "</font>;<br>"
							;

					processListNew.put(info.processName, info.importance);
			 		System.out.println("New infoProcesName"+info.processName);
			        if(processListOld!=null)
			        {
					   if(processListOld.containsKey(info.processName)== false)
					   {
						   AppList += "NEWAPP";
					   }
					   else
					   {
						   if(processListOld.get(info.processName)!= info.importance)
						   {
							   if(info.importance == 100)
							   {
								   ground  = "Taken BG";
							   }
							   else if(info.importance == 400)
							   {
								   ground = "Brought FG";
							   }
							   AppList += ground;
						   }
					   }
			        }
				}
				catch(Exception e)
				{
					System.out.println("EXCEPTOIN\n");
					e.printStackTrace();System.out.println("EXCEPTOIN2\n");
					//Name Not FOund Exception
				}
			}

			//processListOld = new HashMap<String,Integer>();

			if(processListOld!=null)
			{
				Iterator<Entry<String,Integer>> it= processListOld.entrySet().iterator();
				while(it.hasNext())
				{
					Map.Entry<String,Integer> processEntry = it.next();
					if(processListNew.containsKey(processEntry.getKey()) == false)
					{
						try
						{
							AppList+= pm.getApplicationLabel(pm.getApplicationInfo(processEntry.getKey(), PackageManager.GET_META_DATA)) + "closed";
						}
						catch (NameNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			processListOld = processListNew;
			callbackContext.success(AppList);
			return true;
		}
		callbackContext.error("Error in reading Running App List");
		return false;
	}
}
