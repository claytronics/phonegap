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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
public class AppLister extends CordovaPlugin implements LocationListener,SensorEventListener{
	BroadcastReceiver receiver;
	processDetails proDet = null;
	usageTime useTime = null;
	Location location = null;
	accelerometerTrack accTrack = null;
	locationTrack locTrack = null;
	private static HashMap<String,processDetails> processListAll = new HashMap<String,processDetails>();
	private static HashMap<String,Integer> processListNew = null;
	private static HashMap<String,Integer> processListOld = null;
	Boolean alreadyPresent = false;
	Boolean changed = false;
	String ground = "";
	Boolean typeGPS = false;
	Boolean typeNetwork = false;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
		proDet = new processDetails();
		useTime = new usageTime();
		processListNew = new HashMap<String,Integer>();

		if (action.equals("Running")) 
		{

			LocationManager lm = (LocationManager)cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
			SensorManager sm = (SensorManager)cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
			Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			typeGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			typeNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			
			PackageManager pm = this.cordova.getActivity().getPackageManager();
			String AppList = "<br>";
			
			ActivityManager am = (ActivityManager)cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
			Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
			
			while(i.hasNext()) 
			{
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
				
				if(typeGPS)
				{	
					lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,600,10, this);
				    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				    
				    				    
				    System.out.println("TYPEGPS");
				    if(location == null)
				    {
				    	System.out.println("LOCATION IS NULL");	
				    }	
				    else
				    {
				    	System.out.println("LOCTION IS NOT NULL");
				    }
				}    
				else if(typeNetwork)
				{	
					lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,600,10, this);
					location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					System.out.println("TYPENETWORK");
				}
				
				try 
				{
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					boolean AppCategory = (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).flags & ApplicationInfo.FLAG_SYSTEM)!=0;
					String AppType = AppCategory?"SYSTEM":"USER";
					AppList += (AppCategory?"<font color=\"red\">":"<font color=\"blue\">")+ c.toString() + " :" + AppType + "</font>;  <br>" 
							;

					processListNew.put(info.processName, info.importance);
					//System.out.println("New infoProcesName"+info.processName);
					

					if(processListOld == null)
					{
						System.out.println("processListOld is null"+ info.processName);
						proDet = new processDetails();
						useTime = new usageTime();
						locTrack = new locationTrack();
						accTrack = new accelerometerTrack();
                                                
						sm.registerListener(new SensorEventListener() {
					        @Override
					        public void onSensorChanged(SensorEvent event) {

					            accTrack.setX(event.values[0]);
					            accTrack.setY(event.values[1]);
					            accTrack.setZ(event.values[2]);
					            //double total = Math.sqrt(x * x + y * y + z * z);

					        }

					        @Override
					        public void onAccuracyChanged(Sensor sensor, int accuracy) {
					        }

					    }, sensor, SensorManager.SENSOR_DELAY_FASTEST);

						
						proDet.setImportance(info.importance);	
						useTime.setStartTime(System.currentTimeMillis());
						if(proDet.getLlTime().isEmpty())
						{	
							proDet.getLlTime().add(useTime);
						}
						
						locTrack.setLatitude(location.getLatitude());
						locTrack.setLongitude(location.getLongitude());
						
					    if(proDet.getLlLoc().isEmpty())
					    {
					    	proDet.getLlLoc().add(locTrack);
					    }	
					    
					    if(proDet.getLlAcc().isEmpty())
					    {
					    	proDet.getLlAcc().add(accTrack);
					    }	
					    
						processListAll.put(info.processName, proDet);
					}
					
					if(processListOld!=null)
					{	
						proDet = new processDetails();
						useTime = new usageTime();
						locTrack = new locationTrack();
						
						
						if(processListAll.containsKey(info.processName))
						{
							proDet = processListAll.get(info.processName);
						}
						
						proDet.setImportance(info.importance);
						
						sm.registerListener(new SensorEventListener() {
					        @Override
					        public void onSensorChanged(SensorEvent event) {

					            accTrack.setX(event.values[0]);
					            accTrack.setY(event.values[1]);
					            accTrack.setZ(event.values[2]);
					            //double total = Math.sqrt(x * x + y * y + z * z);

					        }

					        @Override
					        public void onAccuracyChanged(Sensor sensor, int accuracy) {
					        }

					    }, sensor, SensorManager.SENSOR_DELAY_FASTEST);

						locTrack.setLatitude(location.getLatitude());
						locTrack.setLongitude(location.getLongitude());
						proDet.getLlLoc().add(locTrack);
						
						proDet.getLlAcc().add(accTrack);
						
						if(processListOld.containsKey(info.processName)== false)
						{
							System.out.println("processListOld is not null"+ info.processName);
						
							useTime.setStartTime(System.currentTimeMillis());
							proDet.getLlTime().add(useTime);
							processListAll.put(info.processName, proDet);
							
							AppList += "NEWAPP <br>";
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
								AppList += ground + "<br>";
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
            //TO CHECK IF APP IS TERMINATED.
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
							
							useTime = proDet.getLlTime().getLast();
							if(useTime != null)
							{
								useTime.setEndTime(System.currentTimeMillis());
								proDet.getLlTime().removeLast();
								proDet.getLlTime().add(useTime);
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
				try 
				{
					System.out.println("PATHIS"+Environment.getExternalStorageDirectory());
					File file = new File(Environment.getExternalStorageDirectory(), "testfile.txt");
					BufferedWriter output = new BufferedWriter(new FileWriter(file));
					
					Iterator<Entry<String,processDetails>> it= processListAll.entrySet().iterator();
					while(it.hasNext())
					{
						Entry<String,processDetails> allEntry= it.next();
						output.write("\n\nprocess Name : "+allEntry.getKey());
						output.write("\nprocess Importance : "+allEntry.getValue().getImportance());
						System.out.println("\n\nprocess Name : "+allEntry.getKey());
						System.out.println("\nprocess Importance : "+allEntry.getValue().getImportance());
						
						LinkedList<usageTime> localLL = allEntry.getValue().getLlTime();
						if(localLL!=null)
						{
						    Iterator<usageTime> itUse = localLL.iterator();
							while(itUse.hasNext())
							{	
								usageTime time = itUse.next();
								output.write("\nstart Time : "+time.getStartTime());
								output.write("\nend Time : "+time.getEndTime());
								System.out.println("\nstart Time : "+time.getStartTime());
								System.out.println("\nend Time : "+time.getEndTime());
							}	 
						}		 
						
						LinkedList<locationTrack> llLoc = allEntry.getValue().getLlLoc();
						if(llLoc!=null)
						{
						    Iterator<locationTrack> itLoc = llLoc.iterator();
							while(itLoc.hasNext())
							{	
								locationTrack locTrack = itLoc.next();
								output.write("\nLatitude : "+locTrack.getLatitude());
								output.write("\nLongitude : "+locTrack.getLongitude());
								System.out.println("\nLatitude : "+locTrack.getLatitude());
								System.out.println("\nLongitude : "+locTrack.getLongitude());
							}	 
						}
						LinkedList<accelerometerTrack> llAcc = allEntry.getValue().getLlAcc();
						if(llAcc!=null)
						{
						    Iterator<accelerometerTrack> itAcc = llAcc.iterator();
							while(itAcc.hasNext())
							{	
								accelerometerTrack accTrack = itAcc.next();
								output.write("\nX-axis : "+accTrack.getX());
								output.write("\nY-axis : "+accTrack.getY());
								output.write("\nZ-axis : "+accTrack.getZ());
								System.out.println("\nX-axis : "+accTrack.getX());
								System.out.println("\nY-axis : "+accTrack.getY());
								System.out.println("\nZ-axis : "+accTrack.getZ());
								
							}	 
						}
					}
					
			          output.close();
					
				} catch (Exception e) {
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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}	
