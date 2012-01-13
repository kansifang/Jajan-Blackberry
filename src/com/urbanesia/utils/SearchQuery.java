package com.urbanesia.utils;

import java.util.Enumeration;
import java.util.Vector;

import net.rim.device.api.ui.UiApplication;

import org.json.waku.JSONArray;
import org.json.waku.JSONException;
import org.json.waku.JSONObject;

import com.urbanesia.api.OAUTHnesia;
import com.urbanesia.beans.SearchResult;
import com.urbanesia.jajan.JajanScreen;
import com.urbanesia.ui.SearchList;

public class SearchQuery extends Thread {
	private static String
		coords, subcat, result;
	private Vector resultList;
	
	public SearchQuery(String coords, String subcat) {
		setSubcat(subcat);
		setCoords(coords);
		resultList = new Vector();
	}
	
	private void updateUI() {
		synchronized(UiApplication.getEventLock()) {
			JajanScreen jscreen = (JajanScreen) UiApplication.getUiApplication().getActiveScreen();
			
			Log.out("Updating UI..");
			
			if(resultList.size() > 0 && jscreen != null) {
				SearchList sl = new SearchList();
				sl.setSize(0);
				jscreen.add(sl);
				jscreen.invalidate();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {}
				
				//Log.out("Populating list..");
				Enumeration e = resultList.elements();
				while(e.hasMoreElements()) {
					SearchResult s = (SearchResult) e.nextElement();
					//Log.out("Row 1 - " + s.getBusinessName());
					sl.addRow(s);
				}
				sl.setSize(resultList.size());
				sl.invalidate();
				jscreen.add(sl);
			}
			jscreen.invalidate();
		}
	}
	
	public void run() {
		OAUTHnesia o = new OAUTHnesia(
				Utils.CONSUMER_KEY,
				Utils.CONSUMER_SECRET,
				Utils.USER_KEY,
				Utils.USER_SECRET,
				OAUTHnesia.OAUTH_SAFE_ENCODE
		);
		setResult(
				o.oAuth(
						"get/super_search", 
						"", 
						"subcat="+
							getSubcat()+
						"&ll="+
							getCoords())+
						"&offset=0&row=50&d=3&s=5"
		);
		//Log.out(getResult());
		
		try {
			//Log.out("Initializing JSON..");
			JSONObject biz = (new JSONObject(getResult())).getJSONObject("biz_profile");
			JSONArray ja = biz.names();
			
			//Log.out("Initializing SearchResult beans..");
			
			//Log.out("Looping through the JSON..");
			for(int i=0, max=biz.length(); i<max; i++) {
				JSONObject biz_prof = biz.getJSONObject(ja.getString(i));
				JSONObject attrs = biz_prof.getJSONObject("attrs");
				
				SearchResult searchResult = new SearchResult();
				//Log.out("Populating beans for \"" + attrs.get("business_name").toString() + "\"");
				
				searchResult.setBusinessName(attrs.get("business_name").toString());
				searchResult.setBusinessAddress1(attrs.get("business_address1").toString());
				searchResult.setBusinessAddress2(attrs.get("business_address2").toString());
				searchResult.setBusinessKecamatan(attrs.get("kecamatan_name").toString());
				searchResult.setBusinessKabupaten(attrs.get("city_name").toString());
				searchResult.setBusinessProvinsi(attrs.get("province_name").toString());
				searchResult.setBusinessKodePos(attrs.get("business_zipcode").toString());
				searchResult.setBusinessDistance(attrs.get("distance_kilometer").toString());
				searchResult.setBusinessReview(attrs.get("reviews").toString());
				searchResult.setBusinessReviewCount(attrs.get("total_reviews").toString());
				searchResult.setBusinessUri(attrs.get("business_uri").toString());
				searchResult.setBusinessMobileWebURL(Utils.MOBILE_WEB_BIZ_PROFILE + searchResult.getBusinessUri());
				searchResult.setBusinessParentName(attrs.get("business_parent_name").toString());
				searchResult.setBusinessParentUri(attrs.get("business_parent_uri").toString());
				searchResult.setBusinessPhoto(attrs.get("use_photo_full").toString());
				searchResult.setBusinessLatitude(attrs.get("latitude").toString());
				searchResult.setBusinessLongitude(attrs.get("longitude").toString());
				
				try {
					int rating = Float.valueOf(attrs.get("rating").toString()).intValue();
					searchResult.setBusinessRating(String.valueOf(rating));
				} catch(Exception e) {
					searchResult.setBusinessRating("0");
				}
				
				resultList.addElement(searchResult);
			}
			//Log.out("JSON loop finished..");
			
			updateUI();
		} catch(JSONException e) {
			//Log.out("JSON Parsing Error: " + e.getMessage());
		}
	}

	public static void setSubcat(String subcat) {
		SearchQuery.subcat = subcat;
	}

	public static String getSubcat() {
		return subcat;
	}

	public static void setCoords(String coords) {
		SearchQuery.coords = coords;
	}

	public static String getCoords() {
		return coords;
	}

	private static void setResult(String result) {
		SearchQuery.result = result;
	}

	public static String getResult() {
		return result;
	}
}
