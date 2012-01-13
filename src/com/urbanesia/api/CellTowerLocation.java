package com.urbanesia.api;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

import net.rim.device.api.system.GPRSInfo;

import org.json.waku.JSONException;
import org.json.waku.JSONObject;

import com.urbanesia.beans.IP2Location;
import com.urbanesia.beans.IP2LocationException;
import com.urbanesia.utils.Log;
import com.urbanesia.utils.Utils;

public class CellTowerLocation {
	protected static final int LOOKUP_TIMEOUT = 100;
	protected static final int LOOKUP_TOLERANCE = 0;
	
	public static final boolean FORCE_LOCATION_UPDATE = true;
	public static final boolean DONT_FORCE_LOCATION_UPDATE = false;
	
	public static String getLocation() {
		Log.out("Coordinates - Using GPS");
		
		Criteria ct = new Criteria();
		ct.setHorizontalAccuracy(1000);
		ct.setVerticalAccuracy(1000);
		ct.setCostAllowed(true);
		ct.setPreferredResponseTime(LOOKUP_TIMEOUT);
		ct.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
		
		String coords = "";
		try {
			LocationProvider lp = LocationProvider.getInstance(ct);
			Location loc = lp.getLocation(LOOKUP_TIMEOUT + LOOKUP_TOLERANCE);
			
			String lat = String.valueOf(loc.getQualifiedCoordinates().getLatitude());
			String lon = String.valueOf(loc.getQualifiedCoordinates().getLongitude());
			
			coords = lat+","+lon;
			Log.out("Coordinates - Exact Location: " + coords);
		} catch (LocationException e) {
			Log.out("Coordinates - Not found from GPS");
			coords = lastKnownLocation();
		} catch (InterruptedException e) {
			Log.out("Coordinates - Not found from GPS");
			coords = lastKnownLocation();
		}
		
		Log.out("Coordinates: " + coords);
		return coords;
	}
	
	public static String getLocation(boolean force) {
		if(force) {
			Criteria ct = new Criteria();
			ct.setHorizontalAccuracy(1000);
			ct.setVerticalAccuracy(1000);
			ct.setCostAllowed(true);
			ct.setPreferredResponseTime(LOOKUP_TIMEOUT);
			ct.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
			
			String coords = "";
			try {
				LocationProvider lp = LocationProvider.getInstance(ct);
				Location loc = lp.getLocation(-1);
				
				String lat = String.valueOf(loc.getQualifiedCoordinates().getLatitude());
				String lon = String.valueOf(loc.getQualifiedCoordinates().getLongitude());
				
				coords = lat+","+lon;
			} catch (Exception e) {
				coords = lastKnownLocation();;
			}
			
			Log.out("Coordinates: " + coords);
			return coords;
		} else {
			return "0,0";
		}
	}
	
	public static String getLocationGMM() {
		String coords = "0,0";
		int cellID = GPRSInfo.getCellInfo().getCellId();
		int lac = GPRSInfo.getCellInfo().getLAC();
		
		String urlString = "http://www.google.com/glm/mmap" + ConnString.getConnectionString();
		
		try {
			HttpConnection conn = (HttpConnection) Connector.open(urlString);
			OutputStream os = null;
			InputStream in = null;
			
			conn.setRequestMethod(HttpConnection.POST);
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
			conn.setRequestProperty("If-Modified-Since","29 Oct 1999 19:43:31 GMT");
			conn.setRequestProperty("User-Agent","Urbanesia Jajan Blackberry v1.0");
			conn.setRequestProperty("Content-Language", "en-US");
			
			os = conn.openOutputStream();
			WriteData(os, cellID, lac);
			
			in = conn.openInputStream();
			DataInputStream dataInputStream = new DataInputStream(in);
			dataInputStream.readShort();
			dataInputStream.readByte();
			int code = dataInputStream.readInt();
			if (code == 0) {
				double lat = (double) dataInputStream.readInt() / 1000000D;
			    double lng = (double) dataInputStream.readInt() / 1000000D;
			    dataInputStream.readInt();
			    dataInputStream.readInt();
			    dataInputStream.readUTF();
			    
			    coords = Double.toString(lat) + "," + Double.toString(lng);
			    Log.out("Coordinates - Got from GMM: " + coords);
			} else {
				Log.out("Coordinates - Not found from GMM");
				coords = lastKnownLocation();
				//if(coords.compareTo("0.0,0.0") == 0) {
				//	coords = getLocation();
				//}
				return coords;
			}
		} catch (Exception e) {
			Log.out("Coordinates - Not found from GMM");
			//if(coords.compareTo("0.0,0.0") == 0) {
			//	coords = getLocation();
			//}
			return coords;
		}
		
		return coords;
	}
	
	private static String lastKnownLocation() {
		Location loc = LocationProvider.getLastKnownLocation();
		String lat = String.valueOf(loc.getQualifiedCoordinates().getLatitude());
		String lon = String.valueOf(loc.getQualifiedCoordinates().getLongitude());
		String coords = lat+","+lon;
		Log.out("Coordinates - Using last known location: " + coords);
		return coords;
	}
	
	public static IP2Location getLocationFromIP(String ip) throws IP2LocationException {
		IP2Location ret = new IP2Location();
		
		OAUTHnesia o = new OAUTHnesia(
				Utils.CONSUMER_KEY,
				Utils.CONSUMER_SECRET,
				Utils.USER_KEY,
				Utils.USER_SECRET,
				OAUTHnesia.OAUTH_SAFE_ENCODE
		);
		
		String res = o.oAuth("get/loc_from_ip", "", "");
		
		try {
			JSONObject loc = new JSONObject(res).getJSONObject("loc_from_ip").getJSONObject("location");
			ret.setCity(loc.get("city").toString());
			ret.setLatitude(loc.get("latitude").toString());
			ret.setLongitude(loc.get("longitude").toString());
		} catch(JSONException e) {
			throw new IP2LocationException("Cannot get location from IP.");
		}
		
		return ret;
	}
	
	public static String Geocode(String loc) {
		String ret = "0,0";
		
		OAUTHnesia o = new OAUTHnesia(
				Utils.CONSUMER_KEY,
				Utils.CONSUMER_SECRET,
				Utils.USER_KEY,
				Utils.USER_SECRET,
				OAUTHnesia.OAUTH_SAFE_ENCODE
		);
		
		String res = o.oAuth("get/geocode", "", "where=" + sanitize_kw(loc));
		//Log.out(res);
		
		try {
			JSONObject geo = new JSONObject(res).getJSONObject("geocode");
			ret = geo.get("latitude").toString() + "," + geo.get("longitude").toString();
		} catch(JSONException e) {}
		
		return ret;
	}
	
	private static String sanitize_kw(String kw) {
		return kw.replace(' ', '+');
	}
	
	private static void WriteData(OutputStream out, int cellID, int lac) throws IOException  {
		DataOutputStream dataOutputStream = new DataOutputStream(out);
		dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cellID);  
        dataOutputStream.writeInt(lac);     

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
	}
	
}
