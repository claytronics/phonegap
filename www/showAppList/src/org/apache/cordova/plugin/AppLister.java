package org.apache.cordova.plugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import java.util.List;
import java.util.Iterator;
import java.lang.CharSequence;

/**
 * This class echoes a string called from JavaScript.
 */
public class AppLister extends CordovaPlugin {
	private static final String TAG = "AppListerJava";
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	    Log.w(TAG, "Start");
        if (action.equals("Running")) {
        	Log.w(TAG, "1");
            ActivityManager am = (ActivityManager)cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            Log.w(TAG, "2");
            List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
            Log.w(TAG, "3");
            Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
            Log.w(TAG, "4");
            PackageManager pm = this.cordova.getActivity().getPackageManager();
			String AppList = "<br>";
			while(i.hasNext()) {
				Log.w(TAG, "5");
	            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
			  try {
				  Log.w(TAG, "6");		            
				CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
				Log.w("LABEL", c.toString());
				boolean AppCategory = (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).flags & ApplicationInfo.FLAG_SYSTEM)!=0;
				String AppType = AppCategory?"SYSTEM":"USER";
				AppList = AppList + (AppCategory?"<font color=\"red\">":"<font color=\"blue\">")+ c.toString() + " :" + AppType + "</font>;<br>";				
			  }catch(Exception e) {
				  Log.w(TAG, "7");		            
				//Name Not FOund Exception				
			  }
			}
			callbackContext.success(AppList);
			return true;
			
        } 
        callbackContext.error("Error in reading Running App List");
		return false;
    }
}	
