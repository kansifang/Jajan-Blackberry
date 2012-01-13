package com.urbanesia.jajan;

import net.rim.blackberry.api.bbm.platform.service.MessagingService;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;

import com.urbanesia.utils.LocationQuery;
import com.urbanesia.utils.Utils;

public final class JajanScreen extends MainScreen {
    public JajanScreen() {
    	setBanner(getJajanTopBar());
        
        startLocation();
    }
    
    private void startLocation() {
    	// Loading..
    	HorizontalFieldManager hfm = LocationQuery.showLoading();
        setStatus(hfm);
        
        LocationQuery lq = new LocationQuery(hfm);
        lq.start();
    }
    
    private void startLocation(boolean fromContextMenu) {
    	// Loading..
    	HorizontalFieldManager hfm = LocationQuery.showLoading();
        setStatus(hfm);
        
        LocationQuery lq = new LocationQuery(hfm, fromContextMenu);
        lq.start();
    }
    
    private HorizontalFieldManager getJajanTopBar() {
    	HorizontalFieldManager hfm = new HorizontalFieldManager();
		hfm.setBackground(BackgroundFactory.createBitmapBackground(EncodedImage.getEncodedImageResource(Utils.TOP_BAR_BG).getBitmap()));
    	hfm.setPadding(4, 0, 4, 0);
    	BitmapField bf = new BitmapField(EncodedImage.getEncodedImageResource(Utils.ICON_SMALL).getBitmap(), Field.FIELD_LEFT);
    	hfm.add(bf);
    	LabelField l = new LabelField(Utils.APP_NAME, Field.USE_ALL_WIDTH | DrawStyle.HCENTER) {
        	public void paint(Graphics g) {
        		g.setBackgroundColor(0x0089c33b);
        		g.setColor(Color.WHITE);
        		g.fillRect(0,0,getWidth(),getHeight());
        		g.clear();
        		super.paint(g);
        	}
        };
        l.setFont(Font.getDefault().derive(Font.PLAIN, 7, Ui.UNITS_pt));
        //l.setMargin(0, 0, 0, 5);
        hfm.add(l);
    	return hfm;
    }
    
    protected void makeMenu(Menu menu, int instance) {
    	super.makeMenu(menu, instance);
    	MenuItem m = new MenuItem("Refresh Location", 100, 0) {
    		public void run() {
    			startLocation();
    		}
    	};
    	menu.add(m);
    	m = new MenuItem("Where am I?", 100, 1) {
    		public void run() {
    			startLocation(true);
    		}
    	};
    	menu.add(m);
    	m = new MenuItem("Invite a BBM Contact", 100, 2) {
    		public void run() {
    			if(Jajan.platformContext != null) {
    				MessagingService messagingService = Jajan.platformContext.getMessagingService();
    				messagingService.sendDownloadInvitation();
    			}
    		}
    	};
    	menu.add(m);
    }
}
