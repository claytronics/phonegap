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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class echoes a string called from JavaScript.
 */

public class AppTracker extends CordovaPlugin implements SensorEventListener{
	private static final String TAG = "AppTrackerJava";
	BroadcastReceiver receiver;
	Float x;
	Float y;
	Float z;
	SensorManager sm;

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
			LocationManager locationManager;
			String provider;
			// Get the location manager
		    locationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
		    // Define the criteria how to select the location provider -> use
		    // default
		    Criteria criteria = new Criteria();
		    provider = locationManager.getBestProvider(criteria, true);
		    Location location = locationManager.getLastKnownLocation(provider);

		    sm = (SensorManager)cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
			Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

			Log.w(TAG, "1");
			ActivityManager am = (ActivityManager)cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			Log.w(TAG, "2");
			List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
			Log.w(TAG, "3");
			Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
			Log.w(TAG, "4");
			//PackageManager pm = this.cordova.getActivity().getPackageManager();

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
						value.Start = getTimeStamp();
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
							value.Start = getTimeStamp();
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
								Log.w(TAG, "6.0");
								if(location != null)
								{
									Double lat = location.getLatitude();
									Double lon = location.getLongitude();
									if(AppDataList == null)
										AppDataList = lat.toString() + '/' + lon.toString() + '/';
									else
										AppDataList += lat.toString() + '/' + lon.toString() + '/';
								}
								else
								{
									if(AppDataList == null)
										AppDataList = "101.2/101.2/";
									else
										AppDataList += "101.2/101.2/";
								}
								Log.w(TAG, "6.1");
							    if(x!=null)
							    	AppDataList += x.toString() + '/' + y.toString() + '/' + z.toString() + '/';
							    else
							    	AppDataList += "null/null/null/";

							    Log.w(TAG, "6.2");
							    AllAppsData.get(info.processName).End = getTimeStamp();
								AllAppsData.get(info.processName).isTerminated = false;
								AppDataList += (AllAppsData.get(info.processName).Name + '/' + AllAppsData.get(info.processName).isFG.toString() + '/' +
								AllAppsData.get(info.processName).importance.toString() + '/' +  (AllAppsData.get(info.processName).Start) + '/' +
								AllAppsData.get(info.processName).End + '/' + AllAppsData.get(info.processName).isTerminated.toString()) + '/';
								Log.w(TAG, "6.3");
								// update the importance and start times; this is a new entry
								AllAppsData.get(info.processName).isFG = (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
								AllAppsData.get(info.processName).importance = info.importance;
								AllAppsData.get(info.processName).Start = getTimeStamp();
								AllAppsData.get(info.processName).End = null;
								Log.w(TAG, "6.4");
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
				LinkedList<String> AppNames = new LinkedList<String>();
				while(it.hasNext())
				{
					Map.Entry<String,appData> processEntry =  it.next();
					AppNames.push(processEntry.getValue().Name);
				}

				ListIterator<String> itr= AppNames.listIterator();

				while(itr.hasNext())
				{
					String AppName = itr.next();

					if(!processListCurr.contains(AppName))
					{
						// app terminated event - since not present is current list of running apps
						// call the callback function to log the event
						Log.w(TAG, "8.0");
						if(location != null)
						{
							Double lat = location.getLatitude();
							Double lon = location.getLongitude();
							if(AppDataList == null)
								AppDataList = lat.toString() + '/' + lon.toString() + '/';
							else
								AppDataList += lat.toString() + '/' + lon.toString() + '/';
						}
						else
						{
							if(AppDataList == null)
								AppDataList = "101.2/101.2/";
							else
								AppDataList += "101.2/101.2/";
						}

						Log.w(TAG, "8.1");
					    if(x!=null)
					    	AppDataList += x.toString() + '/' + y.toString() + '/' + z.toString() + '/';
					    else
					    	AppDataList += "null/null/null/";

					    Log.w(TAG, "8.2");
						AllAppsData.get(AppName).End = getTimeStamp();
						AllAppsData.get(AppName).isTerminated = true;
						AppDataList += (AllAppsData.get(AppName).Name + '/' + AllAppsData.get(AppName).isFG.toString() + '/' +
						AllAppsData.get(AppName).importance.toString() + '/' +  AllAppsData.get(AppName).Start + '/' +
						AllAppsData.get(AppName).End + '/' + AllAppsData.get(AppName).isTerminated.toString()) + '/';
						Log.w(TAG, "8.3");
						// remove the obj from the list
						AllAppsData.remove(AppName);
						Log.w(TAG, "8.4");
					}

				}

			}
			Log.w(TAG, "9.0");
			callbackContext.success(AppDataList);
		}
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		x = (event.values[0]);
        y = (event.values[1]);
        z = (event.values[2]);
        sm.unregisterListener(this);
	}

   public String getTimeStamp()
   {
		Calendar cal = Calendar.getInstance();
       return ((cal.get(Calendar.MONTH) + 1) + '/' + (cal.get(Calendar.DAY_OF_MONTH)) + '/' + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR_OF_DAY) + ':'
                     + ((cal.get(Calendar.MINUTE) < 10) ? ("0" + cal.get(Calendar.MINUTE)) : (cal.get(Calendar.MINUTE))) + ':' + ((cal.get(Calendar.SECOND) < 10) ? ("0" + cal.get(Calendar.SECOND)) : (cal.get(Calendar.SECOND))));
    }
	}