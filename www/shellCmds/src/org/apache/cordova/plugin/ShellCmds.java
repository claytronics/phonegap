package org.apache.cordova.plugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import java.util.concurrent.TimeoutException;
import java.io.IOException;

import com.stericson.RootTools.*;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

/**
 * This class echoes a string called from JavaScript.
 */
public class ShellCmds extends CordovaPlugin {
	private static final String TAG = "ShellCmdsJava";
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	    Log.w(TAG, "Start");
	    if (action.equals("Top")) {
        	Log.w(TAG, "Top Cmd");
			
        	try
			{				
				CommandCapture command = new CommandCapture(0, "top -m 20 -n 1");
				RootTools.getShell(false).add(command).waitForFinish();
				
				callbackContext.success(command.toString());
				return true;
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				callbackContext.error("Error in reading Running App List");
				return false;
			}
        	catch (InterruptedException e) 
        	{
        		Log.w(TAG, "InterruptedException");
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
        else if (action.equals("PS")) {
        	Log.w(TAG, "PS Cmd");
			
        	try
			{				
				CommandCapture command = new CommandCapture(0, "ps");
				RootTools.getShell(false).add(command).waitForFinish();
				
				callbackContext.success(command.toString());
				return true;
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				callbackContext.error("Error in reading Running App List");
				return false;
			}
        	catch (InterruptedException e) 
        	{
        		Log.w(TAG, "InterruptedException");
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
        else if (action.equals("Kill")) {
        	Log.w(TAG, "Kill Cmd");
			//TBD: pass a pid to kill
        	try
			{				
				CommandCapture command = new CommandCapture(0, "/system/bin/kill");
				RootTools.getShell(false).add(command).waitForFinish();
				
				callbackContext.success("");
				return true;
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				callbackContext.error("Error in reading Running App List");
				return false;
			}
        	catch (InterruptedException e) 
        	{
        		Log.w(TAG, "InterruptedException");
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        }
        else if (action.equals("logcat")) {
        	Log.w(TAG, "logcat Cmd");
			
        	try
			{				
        		//String filename = String.valueOf(System.currentTimeMillis()) + ".txt";
        		String filename = "/data/local/tmp/logcat.txt";
        		        		
				CommandCapture command = new CommandCapture(0, "logcat -d -v long ");
				RootTools.getShell(false).add(command).waitForFinish();
								
				callbackContext.success(command.toString() + "\n\n*** logcat dumped to file " + filename + "***\n\n");
				
				command = new CommandCapture(0, "cat " + command.toString() + " > " + filename);
				RootTools.getShell(false).add(command).waitForFinish();
				
				return true;
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				callbackContext.error("Error in reading Running App List");
				return false;
			}
        	catch (InterruptedException e) 
        	{
        		Log.w(TAG, "InterruptedException");
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
        return false;
	}	
}