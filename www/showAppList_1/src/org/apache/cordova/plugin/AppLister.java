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

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.CharSequence;

import com.stericson.RootTools.*;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

/**
 * This class echoes a string called from JavaScript.
 */

public class AppLister extends CordovaPlugin {
	private static final String TAG = "AppListerJava";
	BroadcastReceiver receiver;
	Boolean alreadyPresent = false;
	Boolean changed = false;
	String ground = "";

	public class interval{
		Calendar starttime;
		Calendar endtime;
	};

	public class appData{
		public String Name;
		private LinkedList<interval> quants; // this has the list of all start and end times of the app
		public String Type; // user or system?
		private int importance;
		// more to be added here
		
		public appData()
		{
			quants = new LinkedList<interval>();
		};
		
		public String getImportance(){
			if(importance ==   ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) return "FG";
			else if(importance ==   ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) return "BG";
			return "";
		};
		
		public boolean logStartTime(Calendar now){
			interval currT, prevT;
			
			currT = new interval();
			
			currT.starttime = now;
			currT.endtime = now; // just being safe
			try{
			prevT = quants.getLast();
			
			if((prevT.starttime.before(now)) && (prevT.endtime.before(now)))
			{
				quants.add(currT);
				return true;				
			}
			else
				return false;
			}
			catch(NoSuchElementException exception){
				quants.add(currT);
				return true;		
			}
		};
		
		public boolean logEndTime(Calendar now){
			interval currT, prevT;
			try{
				prevT = quants.getLast();
				
				currT = new interval();
				currT.starttime = prevT.starttime;
				currT.endtime = now;
				
				boolean before = prevT.starttime.before(now);
				boolean equals = prevT.endtime.equals(prevT.starttime);
				if(prevT.starttime.before(now) && (prevT.endtime.equals(prevT.starttime)))
				{
					quants.removeLast();
					quants.add(currT);
					return true;				
				}
				else
					return false;
			}
			catch(NoSuchElementException exception){
				return false;
			}
			
		};
		
	};	
	
	private static LinkedList<String> processListNew;
	private static LinkedList<String> processListOld;
	
	private static HashMap<String,appData> AllAppsData = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.w(TAG, "Start");

		if(null == processListNew)
			processListNew = new LinkedList<String>();

		/*if(null == processListOld)
			processListOld = new LinkedList<String>();*/
	
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
			
			if(AllAppsData==null)
			{
				AllAppsData = new HashMap<String,appData>();
				
				Log.w(TAG, "5");
				if(processListNew != null)
					processListNew.clear();
				else
					processListNew = new LinkedList<String>();
				
				while(i.hasNext())
				{
					Log.w(TAG, "5.1");
					ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
					try
					{
						Log.w(TAG, "5.2");
						CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
						Log.w(TAG, "5.3");
						//Log.w("LABEL", c.toString());
						boolean AppCategory = (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).flags & ApplicationInfo.FLAG_SYSTEM)!=0;
						String AppType = AppCategory?"SYSTEM":"USER";
						AppList += (AppCategory?"<font color=\"red\">":"<font color=\"blue\">")+ c.toString() + " :" + AppType + "</font>;<br>";
						Log.w(TAG, "5.4");
						processListNew.add(info.processName);						
						Log.w(TAG, "5.4.1");
						appData value = new appData();
						Calendar c1 = Calendar.getInstance(); 
						//int seconds = c1.get(Calendar.SECOND);
						Log.w(TAG, "5.5");		
						
						value.Name = info.processName;
						boolean ret = value.logStartTime(c1);
						if(ret == false)
						{
							Log.w(TAG, "something wrong with logging start time");
						}
						Log.w(TAG, "5.6");
						value.importance = info.importance;
						value.Type = AppType;
						Log.w(TAG, "5.7");
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
				//processListOld.clear();
				processListOld = (LinkedList<String>)processListNew.clone();				
			}
			else
			{
				processListNew.clear();
				Log.w(TAG, "7");
				while(i.hasNext())
				{
					ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
					try
					{
						CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
						//Log.w("LABEL", c.toString());
						boolean AppCategory = (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).flags & ApplicationInfo.FLAG_SYSTEM)!=0;
						String AppType = AppCategory?"SYSTEM":"USER";
						AppList += (AppCategory?"<font color=\"red\">":"<font color=\"blue\">")+ c.toString() + " :" + AppType + "</font>;<br>";
						
						Log.w(TAG, "7.1");
						processListNew.add(info.processName);						
						Log.w(TAG, "7.2");
						
						if(processListOld.contains(info.processName) == false)
						{	
							Log.w(TAG, "7.3");
							Calendar c1 = Calendar.getInstance(); 
							//int seconds = c1.get(Calendar.SECOND);
							
							if(AllAppsData.containsKey(info.processName) == false)
							{	
								appData value = new appData();
								value.Name = info.processName;
								boolean ret = value.logStartTime(c1);
								if(ret == false)
								{
									Log.w(TAG, "something wrong with logging start time");
								}
								value.importance = info.importance;
								value.Type = AppType;
								AllAppsData.put(info.processName, value);
							}
							else
							{	
								boolean ret = AllAppsData.get(info.processName).logStartTime(c1);
								if(ret == false)
								{
									Log.w(TAG, "something wrong with logging start time");
								}
							}	
							Log.w(TAG, "7.4");							
						}
						else // app is already running
						{
							// if fg <-> bg switch
							/*if((AllAppsData.get(info.processName).importance != info.importance))
							{
									Log.w(TAG, "Changed importance");
							}*/

							// some other stuff will go here
						}	
					}
					catch(Exception e)
					{
						System.out.println("EXCEPTOIN\n");
						e.printStackTrace();System.out.println("EXCEPTOIN2\n");
						//Name Not FOund Exception
					}
				}								
			}	
			
			if(processListOld.size() != 0)
			{
				Log.w(TAG, "8");
				int i1=0, size = processListOld.size();
				while(i1 < size)
				{
					if(processListNew.contains(processListOld.get(i1)) == false)
					{
						// means app ended so update the app list
						Calendar c1 = Calendar.getInstance(); 
						//int seconds = c1.get(Calendar.SECOND);
						
						boolean ret = AllAppsData.get(processListOld.get(i1)).logEndTime(c1);
						if(ret == false)
						{
							Log.w(TAG, "something wrong with logging start time");
						}
					}
					i1++;
				}
				
			}
			
			processListOld.clear();
			processListOld = (LinkedList<String>)processListNew.clone();
			
			if(AllAppsData !=null)
			{
				Log.w(TAG, "9");
				try {
					Log.w(TAG, "10");
					CommandCapture command = new CommandCapture(0, "echo $'\n' > /data/local/tmp/AppData.csv");
					RootTools.getShell(false).add(command).waitForFinish();
					
					Log.w(TAG,command.toString());
					
					
					Log.w(TAG, "11");				
					Iterator<Entry<String,appData>> it= AllAppsData.entrySet().iterator();
					while(it.hasNext())
					{
						Log.w(TAG, "12");
						Map.Entry<String,appData> processEntry = it.next();
						
						command = new CommandCapture(0, "echo $'\n'" 
										+  AllAppsData.get(processEntry.getKey()).Name + 
										" , >> /data/local/tmp/AppData.csv");
						RootTools.getShell(false).add(command).waitForFinish();
						Log.w(TAG,command.toString());
						Log.w(TAG, AllAppsData.get(processEntry.getKey()).Name);
						
						int indx=0;
						Integer thisQuant = 0;
						while(indx < AllAppsData.get(processEntry.getKey()).quants.size())
						{
							Log.w(TAG, "13");
							thisQuant += (int)(AllAppsData.get(processEntry.getKey()).quants.get(indx).endtime.getTimeInMillis() 
									    - AllAppsData.get(processEntry.getKey()).quants.get(indx).starttime.getTimeInMillis());
							indx++;
						}		
						command = new CommandCapture(0, "echo " 
								+  thisQuant.toString() + 
								" , >> /data/local/tmp/AppData.csv");
						RootTools.getShell(false).add(command).waitForFinish();
						Log.w(TAG,command.toString());
						Log.w(TAG, thisQuant.toString());
					}
								           
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RootDeniedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			Log.w(TAG, "14");
			callbackContext.success(AppList);
			return true;
		}
		callbackContext.error("Error in reading Running App List");
		return false;
	}
}
