package com.urbanesia.ui;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public class ColorLabel extends LabelField {
	private int color, bg;
	
	public ColorLabel(Object text, long layout, int color, int bg) {
		super(text, layout);
		if(color > 0) {
			this.color = color;
		} else {
			this.color = Color.BLACK;
		}
		
		if(bg > 0) {
			this.bg = bg;
		} else {
			this.bg = Color.WHITE;
		}
	}
	
	public void paint(Graphics g) {
		g.setBackgroundColor(bg);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(color);
		g.clear();
		super.paint(g);
	}
}
