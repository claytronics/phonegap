package org.apache.cordova.plugin;


import org.apache.cordova.api.CallbackContext;

import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class echoes a string called from JavaScript.
 */

public class AppTracker extends CordovaPlugin {
	private static final String TAG = "AppTrackerJava";
	BroadcastReceiver receiver;
	
	public class appData{
		public String Name;
		public String Type; // user or system?
		public Boolean isFG;
		public Integer importance;
		public String Start;
		public String End;
		public Boolean isTerminated;			
	};	
	
	private static LinkedList<String> processListCurr;
	//private static LinkedList<String> processListOld;
	
	private static HashMap<String,appData> AllAppsData = null;
		
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.w(TAG, "Start");
		
		if (action.equals("Running"))
		{
			//Log.w(TAG, "1");
			ActivityManager am = (ActivityManager)cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			//Log.w(TAG, "2");
			List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
			//Log.w(TAG, "3");
			Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
			//Log.w(TAG, "4");
			PackageManager pm = this.cordova.getActivity().getPackageManager();
			
			String AppDataList = null;
			if(AllAppsData==null)
			{
				AllAppsData = new HashMap<String,appData>();
				
				Log.w(TAG, "5");
				if(processListCurr != null)
					processListCurr.clear();
				else
					processListCurr = new LinkedList<String>();
				
				while(i.hasNext())
				{
					Log.w(TAG, "5.1");
					ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
					try
					{
						Log.w(TAG, "5.2");
						Log.w(TAG, "5.3");
						//Log.w("LABEL", c.toString());
						//boolean AppCategory = (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).flags & ApplicationInfo.FLAG_SYSTEM)!=0;
						//String AppType = AppCategory?"SYSTEM":"USER";
						Log.w(TAG, "5.4");
						processListCurr.add(info.processName);						
						Log.w(TAG, "5.4.1");
						appData value = new appData();
						//int seconds = c1.get(Calendar.SECOND);
						Log.w(TAG, "5.5");		
						
						value.Name = info.processName;
						value.Start = new Date().toString();
						value.isFG = (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
						value.isTerminated = false;
						value.importance = info.importance;
						//value.Type = AppType;
						AllAppsData.put(info.processName, value);
						Log.w(TAG, "5.8");
					}
					catch(Exception e)
					{
						Log.w(TAG, "6");
						System.out.println("EXCEPTOIN\n");
						e.printStackTrace();System.out.println("EXCEPTOIN2\n");
						//Name Not FOund Exception
					}
				}								
			}
			else
			{
				processListCurr.clear();
				Log.w(TAG, "7");
				while(i.hasNext())
				{
					ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
					try
					{
						//Log.w("LABEL", c.toString());
						//boolean AppCategory = (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).flags & ApplicationInfo.FLAG_SYSTEM)!=0;
						//String AppType = AppCategory?"SYSTEM":"USER";
						Log.w(TAG, "7.1");
						processListCurr.add(info.processName);						
						Log.w(TAG, "7.2");
						
						// check if new apps
						if(AllAppsData.containsKey(info.processName) == false)
						{	
							appData value = new appData();
							value.Name = info.processName;
							value.Start = new Date().toString();
							value.isFG = (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
							value.isTerminated = false;
							value.importance = info.importance;
							//value.Type = AppType;
							AllAppsData.put(info.processName, value);
						}
						else // check for existing apps change in status
						{
							if(info.importance != AllAppsData.get(info.processName).importance) // check if importance changed
							{
								// app importance changed event
								// call the callback function to log the event
								AllAppsData.get(info.processName).End = new Date().toString();
								AllAppsData.get(info.processName).isTerminated = false;
								AppDataList += (AllAppsData.get(info.processName).Name + '/' + AllAppsData.get(info.processName).isFG.toString() + '/' + 
								AllAppsData.get(info.processName).importance.toString() + '/' +  (AllAppsData.get(info.processName).Start) + '/' + 
								AllAppsData.get(info.processName).End + '/' + AllAppsData.get(info.processName).isTerminated.toString()) + '/';
								
								// update the importance and start times; this is a new entry
								AllAppsData.get(info.processName).isFG = (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
								AllAppsData.get(info.processName).importance = info.importance;
								AllAppsData.get(info.processName).Start = new Date().toString();
								AllAppsData.get(info.processName).End = null;
							}							
						}	
							Log.w(TAG, "7.4");							
						}
						catch(Exception e)
						{
							System.out.println("EXCEPTOIN\n");
							e.printStackTrace();System.out.println("EXCEPTOIN2\n");
							//Name Not FOund Exception
						}
					}
				// check for terminated apps
				Iterator<Entry<String, appData>> it= AllAppsData.entrySet().iterator();
				
				while(it.hasNext())
				{
					Map.Entry<String,appData> processEntry =  it.next();
					if(!processListCurr.contains(processEntry.getValue().Name))
					{
						// app terminated event - since not present is current list of running apps
						// call the callback function to log the event
						AllAppsData.get(processEntry.getValue().Name).End = new Date().toString();
						AllAppsData.get(processEntry.getValue().Name).isTerminated = true;
						AppDataList += (AllAppsData.get(processEntry.getValue().Name).Name + '/' + AllAppsData.get(processEntry.getValue().Name).isFG.toString() + '/' + 
						AllAppsData.get(processEntry.getValue().Name).importance.toString() + '/' +  AllAppsData.get(processEntry.getValue().Name).Start + '/' + 
						AllAppsData.get(processEntry.getValue().Name).End + '/' + AllAppsData.get(processEntry.getValue().Name).isTerminated.toString()) + '/';
						
						// remove the obj from the list
						AllAppsData.remove(processEntry.getValue().Name);
					}
				
				}

			}	
			callbackContext.success(AppDataList);			
		}
		return true;
	}	
}