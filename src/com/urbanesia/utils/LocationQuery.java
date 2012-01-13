package com.urbanesia.utils;

import net.rim.blackberry.api.bbm.platform.profile.Presence;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

import org.json.waku.JSONArray;
import org.json.waku.JSONException;
import org.json.waku.JSONObject;

import com.urbanesia.api.CellTowerLocation;
import com.urbanesia.api.OAUTHnesia;
import com.urbanesia.jajan.Jajan;
import com.urbanesia.jajan.JajanScreen;
import com.urbanesia.ui.InputDialog;

public class LocationQuery extends Thread {
	private HorizontalFieldManager hfm;
	private String coords, coords_loc;
	private InputDialog diag;
	private boolean fromContextMenu;
	
	public static final String LOOKING_UP_LOCATION = "Mencari lokasi anda..";
	
	public LocationQuery(HorizontalFieldManager hfm) {
		this.hfm = hfm;
		this.coords = "";
		this.coords_loc = "";
	}
	
	public LocationQuery(HorizontalFieldManager hfm, boolean fromContextMenu) {
		this.hfm = hfm;
		this.coords = "";
		this.coords_loc = "";
		this.fromContextMenu = fromContextMenu;
	}
	
	public static HorizontalFieldManager showLoading() {
		HorizontalFieldManager hfm = new HorizontalFieldManager();
		hfm.setBackground(BackgroundFactory.createSolidBackground(0x00444444));
    	hfm.setPadding(4, 0, 4, 0);
    	LabelField l = new LabelField(LOOKING_UP_LOCATION, Field.USE_ALL_WIDTH | DrawStyle.HCENTER) {
        	public void paint(Graphics g) {
        		g.setBackgroundColor(0x00444444);
        		g.setColor(Color.WHITE);
        		g.fillRect(0,0,getWidth(),getHeight());
        		g.clear();
        		super.paint(g);
        	}
        };
        l.setFont(Font.getDefault().derive(Font.PLAIN, 4, Ui.UNITS_pt));
        l.setMargin(0, 0, 0, 5);
        hfm.add(l);
        return hfm;
	}
	
	private void updateUI() {
		synchronized(UiApplication.getEventLock()) {
			JajanScreen jscreen = (JajanScreen) UiApplication.getUiApplication().getActiveScreen();
			
			if(jscreen != null) {
				Log.out("Updating nearby texts..");
				hfm.deleteAll();
				String _nearbyLabel = "";
				if(coords_loc.compareTo("...") == 0) {
					_nearbyLabel = "Tidak dapat menemukan lokasi anda..";
				} else {
					_nearbyLabel = "Sekitar " + coords_loc;
				}
				LabelField l = new LabelField(_nearbyLabel, Field.USE_ALL_WIDTH | DrawStyle.HCENTER) {
		        	public void paint(Graphics g) {
		        		g.setBackgroundColor(0x00444444);
		        		g.setColor(Color.WHITE);
		        		g.fillRect(0,0,getWidth(),getHeight());
		        		g.clear();
		        		super.paint(g);
		        	}
		        };
		        l.setFont(Font.getDefault().derive(Font.PLAIN, 4, Ui.UNITS_pt));
		        l.setMargin(0, 0, 0, 5);
		        hfm.add(l);
		        jscreen.add(hfm);
		        jscreen.invalidate();
			} else {
				Log.out("Screen grabbing failed..");
			}
		};
	}
	
	public void run() {
		if(!fromContextMenu) {
			coords = CellTowerLocation.getLocationGMM();
			//coords = "-6.253065,106.800241";
			if(coords.compareTo("0.0,0.0") == 0 || coords.compareTo("0,0") == 0) {
				Log.out("Bad result from GMM");
				//coords = "-6.17535,106.827257";
				String choices[] = {"OK"};
				int values[] = {Dialog.OK};
				
				diag = new InputDialog(choices, values, "Where are you?");
				UiApplication.getUiApplication().invokeLater(
						new Runnable() {
							public void run() {
								if(diag.doModal() == Dialog.OK) {
									coords = CellTowerLocation.Geocode(diag.getInputText());
									Log.out("Coordinates from User Input: " + coords);
									
									locUpdater(diag.getInputText());
								}
							}
						}
				);
			} else {
				locUpdater();
			}
		} else {
			String choices[] = {"OK"};
			int values[] = {Dialog.OK};
			
			diag = new InputDialog(choices, values, "Where are you?");
			UiApplication.getUiApplication().invokeLater(
					new Runnable() {
						public void run() {
							if(diag.doModal() == Dialog.OK) {
								coords = CellTowerLocation.Geocode(diag.getInputText());
								Log.out("Coordinates from User Input: " + coords);
								
								locUpdater(diag.getInputText());
							}
						}
					}
			);
		}
	}
	
	private void locUpdater(String where) {
		// Execute Search!
		SearchQuery sq = new SearchQuery(coords, Utils.SEARCH_SUBCAT);
    	sq.start();
    	
    	// BBM Status
    	Jajan.changeUserBBMStatus(Presence.STATUS_AVAILABLE, "Looking for JAJAN near " + where);
    	
    	coords_loc = where;
    	updateUI();
	}
	
	private void locUpdater() {
		// Execute Search!
		SearchQuery sq = new SearchQuery(coords, Utils.SEARCH_SUBCAT);
    	sq.start();
		
		OAUTHnesia o = new OAUTHnesia(
				Utils.CONSUMER_KEY,
				Utils.CONSUMER_SECRET,
				Utils.USER_KEY,
				Utils.USER_SECRET,
				OAUTHnesia.OAUTH_SAFE_ENCODE
		);
		String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+coords+"&sensor=true";
		String res = o.sendRequest(url, "");
		Log.out("Reverse Geocoding Query Done");
		
		try {
			String status = new JSONObject(res).get("status").toString();
			if(status.toLowerCase().compareTo("ok") == 0) {
				JSONArray rgeo = new JSONObject(res).getJSONArray("results");
				JSONObject loc = rgeo.getJSONObject(0);
				coords_loc = loc.getJSONArray("address_components").getJSONObject(0).get("short_name").toString();
				
				Log.out("Position from Google Maps near " + coords_loc);
			} else {
				reverseGeoUrb(o);
			}
		} catch (JSONException e) {
			Log.out("Location JSON Error..");
			reverseGeoUrb(o);
		}
		
		updateUI();
	}
	
	private void reverseGeoUrb(OAUTHnesia o) {
		try {
			Log.out("Reverse Geocoding returned bad results, switch to Urbanesia!");
			String urb = o.oAuth("get/reverse_geo", "", "ll=" + coords);
			JSONObject urble = new JSONObject(urb).getJSONObject("reverse_geo");
			try {
				JSONArray kel = urble.getJSONArray("kelurahan");
				coords_loc = kel.getJSONObject(0).get("kelurahan_name").toString();
			} catch(JSONException e1) {
				try {
					JSONArray kel = urble.getJSONArray("kecamatan");
					coords_loc = kel.getJSONObject(0).get("kecamatan_name").toString();
				} catch(JSONException e2) {
					JSONArray kel = urble.getJSONArray("province");
					coords_loc = kel.getJSONObject(0).get("province_name").toString();
				}
			}
			
			Log.out("Position from Urbanesia near " + coords_loc);
		} catch(JSONException e) {
			coords_loc = "...";
		}
	}
}
