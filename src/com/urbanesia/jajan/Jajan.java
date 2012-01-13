package com.urbanesia.jajan;

import com.urbanesia.utils.Log;
import com.urbanesia.utils.Utils;

import net.rim.blackberry.api.bbm.platform.BBMPlatformApplication;
import net.rim.blackberry.api.bbm.platform.BBMPlatformContext;
import net.rim.blackberry.api.bbm.platform.BBMPlatformContextListener;
import net.rim.blackberry.api.bbm.platform.BBMPlatformManager;
import net.rim.blackberry.api.bbm.platform.profile.UserProfile;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class Jajan extends UiApplication {
	private JajanBBMPlugin _plugin;
	public static BBMPlatformContext platformContext;
	
    public static void main(String[] args) {
        Jajan theApp = new Jajan();       
        theApp.enterEventDispatcher();
    }

    public Jajan() {
    	_plugin = new JajanBBMPlugin(Utils.JAJAN_UUID);
        pushScreen(new JajanScreen());
        invokeLater(new Runnable() {
        	public void run() {
        		platformContext = BBMPlatformManager.register(_plugin);
        		JajanBBMPlatformContextListener platformContextListener;
        		platformContextListener = new JajanBBMPlatformContextListener();
        		platformContext.setListener(platformContextListener);
        	}
        });
    }
    
    public static boolean changeUserBBMPersonalMessage(String msg) {
    	if(platformContext != null) {
    		UserProfile me = platformContext.getUserProfile();
    		return me.setPersonalMessage(msg);
    	} else {
    		Dialog.alert(Utils.BBM_CONNECTED_YET);
    		return false;
    	}
    }
    
    public static boolean changeUserBBMStatus(int status, String msg) {
    	if(platformContext != null) {
    		UserProfile me = platformContext.getUserProfile();
    		return me.setStatus(status, msg);
    	} else {
    		Dialog.alert(Utils.BBM_CONNECTED_YET);
    		return false;
    	}
    }
    
    private class JajanBBMPlugin extends BBMPlatformApplication {

		public JajanBBMPlugin(String uuid) {
			super(uuid);
		}
    	
    }
    
    private class JajanBBMPlatformContextListener extends BBMPlatformContextListener {

		public void accessChanged(boolean isAccessAllowed, int accessErrorCode) {
			if (!isAccessAllowed) {
				Log.out("Cannot access BBM..");
			}
		}
		
		public void appInvoked(int reason, Object param) {
			
		}
    	
    }
}
