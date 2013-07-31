package org.apache.cordova.plugin;


import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class phoneStateListener extends CordovaPlugin {

	private static final String TAG = "PhoneStateChangeListener";
	private final String NONE = "NONE";
	private Context context;
	private CallbackContext callbackContext;
	private BroadcastReceiver receiver = null;

	/**
	 * Expecting no parameters from javascript
	 * Expecting only success callback from javascript
	 */
	@Override
	public boolean execute(final String action,final JSONArray args, final CallbackContext callbackContext) throws JSONException {	

		this.context = cordova.getActivity().getApplicationContext();

		System.out.println("In phone State Listener");
		if ("start".equals(action)) {
			this.callbackContext = callbackContext;
			startPhoneListener();
			return true;

		}else if ("stop".equals(action)) {
			removePhoneListener();
			this.callbackContext = null;
			return true;
		}

		return false;
	}

	/**
	 * creates a new BroadcastReceiver to listen whether the Telephony State changes
	 */
	public void startPhoneListener() 
	{

		System.out.println("In start phone listener");
		System.out.println("receiver"+this.receiver);
		
			System.out.println("RECEIVER is NULL");
			this.receiver = new BroadcastReceiver() 
			{
              
				@Override
				public void onReceive(final Context context, final Intent intent) 
				{	
					System.out.println("on Receive");
					if (intent != null)
					{	
					  String mode = null;
					  String state = null;
					  String number = null;
					  
					  System.out.println("Action"+intent.getAction());
					  if(intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
   					  {
						  System.out.println("ACTION PHONE STATE CHANGED");
						  mode = "CALL";
						 
						  state = intent.hasExtra(TelephonyManager.EXTRA_STATE) ? intent.getStringExtra(TelephonyManager.EXTRA_STATE) : NONE;
 						 
							if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
							{
								System.out.println("TRYING TO GET NO.");
								number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
							}
					  }
					  	
					  if(intent.getAction().equals(android.media.AudioManager.RINGER_MODE_CHANGED_ACTION))
					  {  
						  mode = "RINGER";
						  state = "RINGER STATUS CHANGED";
					  }	  
					  		
					 		final JSONObject data = new JSONObject();
							try
							  {
								data.put("state", state);
								data.put("number", number);
						      }
							catch(final JSONException e){};
							callbackContext.success(data);
					  
				  }
				}
			};

            this.context.registerReceiver(this.receiver, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
            //this.context.registerReceiver(this.receiver, new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION));		
	}
    
	/**
	 * removes the Receiver
	 */
	private void removePhoneListener() {
		if (this.receiver != null) {
			try {
				this.context.unregisterReceiver(this.receiver);
				this.receiver = null;
			} catch (final Exception e) {
				Log.e(TAG, "Error unregistering phone listener receiver: " + e.getMessage(), e);
			}
		}
	}

	@Override
	public void onDestroy() {
		removePhoneListener();
	}
}