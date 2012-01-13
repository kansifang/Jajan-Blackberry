package com.urbanesia.ui;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class Nearby extends HorizontalFieldManager {
	
	protected static final String NEARBY_DEFAULT = "Near ";
	protected static final int PADDING = 5;
	private String nearbyText;
	
	public Nearby(String n) {
		super(Field.FIELD_HCENTER | Field.FIELD_VCENTER);
		this.setNearbyText(NEARBY_DEFAULT + n);
	}
	
	public void paint(Graphics g) {
		g.setBackgroundColor(Color.WHITE);
		g.clear();
	}

	public void setNearbyText(String nearbyText) {
		this.nearbyText = nearbyText;
	}

	public String getNearbyText() {
		return nearbyText;
	}
	
}
