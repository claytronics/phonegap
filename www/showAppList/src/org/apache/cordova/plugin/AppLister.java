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
import android.os.Environment;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.CharSequence;

/**
 * This class echoes a string called from JavaScript.
 */
public class AppLister extends CordovaPlugin {
	private static final String TAG = "AppListerJava";
	private static int value =1;
	BroadcastReceiver receiver;
	processDetails proDet = null;
	usageTime useTime = null;
	private static HashMap<String,processDetails> processListAll = new HashMap<String,processDetails>();
	private static HashMap<String,Integer> processListNew = null;
	private static HashMap<String,Integer> processListOld = null;
	Boolean alreadyPresent = false;
	Boolean changed = false;
	String ground = "";

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		proDet = new processDetails();
		useTime = new usageTime();
		processListNew = new HashMap<String,Integer>();

		if (action.equals("Running"))
		{

			ActivityManager am = (ActivityManager)cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
			Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
			PackageManager pm = this.cordova.getActivity().getPackageManager();
			String AppList = "<br>";

			while(i.hasNext())
			{
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
				try
				{
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					boolean AppCategory = (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).flags & ApplicationInfo.FLAG_SYSTEM)!=0;
					String AppType = AppCategory?"SYSTEM":"USER";
					AppList += (AppCategory?"<font color=\"red\">":"<font color=\"blue\">")+ c.toString() + " :" + AppType + "</font>;<br>"
							;

					processListNew.put(info.processName, info.importance);
					//System.out.println("New infoProcesName"+info.processName);

					if(processListOld == null)
					{
						System.out.println("processListOld is null"+ info.processName);
						System.out.println("Thread name"+Thread.currentThread().getName());

						proDet = new processDetails();
						useTime = new usageTime();

						proDet.setImportance(info.importance);
						useTime.setStartTime(System.currentTimeMillis());
						if(proDet.getLl().isEmpty())
						{
							proDet.getLl().add(useTime);
						}
						processListAll.put(info.processName, proDet);
					}

					if(processListOld!=null)
					{
						if(processListOld.containsKey(info.processName)== false)
						{
							System.out.println("processListOld is not null"+ info.processName);
							System.out.println("Thread name"+Thread.currentThread().getName());

							proDet = new processDetails();
							useTime = new usageTime();

							if(processListAll.containsKey(info.processName))
							{
								proDet = processListAll.get(info.processName);
							}
							proDet.setImportance(info.importance);

							useTime.setStartTime(System.currentTimeMillis());
							proDet.getLl().add(useTime);
							processListAll.put(info.processName, proDet);

							AppList += "NEWAPP" + System.currentTimeMillis();
						}
						else
						{
							if(processListOld.get(info.processName)!= info.importance)
							{
								//proDet = processListAll.get(info.processName);
								//proDet.setImportance(info.importance);
								//processListAll.put(info.processName, proDet);

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
				Iterator<Entry<String, Integer>> it= processListOld.entrySet().iterator();

				while(it.hasNext())
				{
					Map.Entry<String,Integer> processEntry =  it.next();
					if(processListNew.containsKey(processEntry.getKey()) == false)
					{
						try
						{
							System.out.println("processListnew is null"+ processEntry.getKey());
							System.out.println("Thread name"+Thread.currentThread().getName());

							proDet = new processDetails();
							useTime = new usageTime();

							proDet = processListAll.get(processEntry.getKey());
							useTime = proDet.getLl().getLast();
							if(useTime != null)
							{
								useTime.setEndTime(System.currentTimeMillis());
								proDet.getLl().removeLast();
								proDet.getLl().add(useTime);
								processListAll.put(processEntry.getKey(), proDet);
							}

							AppList+= pm.getApplicationLabel(pm.getApplicationInfo(processEntry.getKey(), PackageManager.GET_META_DATA))
									+ "closed";
						}
						catch (NameNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}


			processListOld = new HashMap<String,Integer>();
			processListOld.putAll(processListNew);


			if(processListAll!= null)
			{

				try {
					System.out.println("PATHIS"+Environment.getExternalStorageDirectory());
					File file = new File(Environment.getExternalStorageDirectory(), "testfile.txt");
					BufferedWriter output = new BufferedWriter(new FileWriter(file));
					//System.out.println("processList"+processListAll);
					Iterator<Entry<String,processDetails>> it= processListAll.entrySet().iterator();
					while(it.hasNext())
					{
						Entry<String,processDetails> allEntry= it.next();
						output.write("\n\nprocess Name : "+allEntry.getKey());
						output.write("process Importance : "+allEntry.getValue().getImportance());

						LinkedList<usageTime> localLL = allEntry.getValue().getLl();
						if(localLL!=null)
						{
						    Iterator<usageTime> itUse = localLL.iterator();
							while(itUse.hasNext())
							{
								usageTime time = itUse.next();
								output.write("start Time : "+time.getStartTime());
								output.write("end Time : "+time.getEndTime());
							}
						}
					}

			          output.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			callbackContext.success(AppList);
			return true;
		}
		callbackContext.error("Error in reading Running App List");
		return false;
	}
}
