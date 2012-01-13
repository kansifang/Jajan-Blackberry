package com.urbanesia.api;

import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.WLANInfo;

public final class ConnString {
	
	public static String getConnectionString() {
		String connString = "";
		
		try {
			if(DeviceInfo.isSimulator()) {
				return "";
			} else if(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED) {
				connString = ";interface=wifi";
			} else if(CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B)) {
				connString = ";deviceside=false;ConnectionType=mds-public";
			} else if(CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT)) {
				connString = ";deviceside=false";
			}
		} catch(Exception e) {
			
		}
		
		//Log.out(connString);
		
		return connString + ";ConnectionTimeout=45000";
	}
	
}
