package com.urbanesia.ui;

import java.util.Vector;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

import com.urbanesia.beans.SearchResult;
import com.urbanesia.utils.Utils;

public class SearchList extends ListField implements ListFieldCallback {
	private static Vector rows;
	protected final static int DEFAULT_ROW_HEIGHT = 32;
	
	public SearchList() {
		setEmptyString("* mencari jajanan.. *", DrawStyle.HCENTER);
		setCallback(this);
		setRowHeight(DEFAULT_ROW_HEIGHT);
		rows = new Vector();
	}
	
	public void drawListRow(ListField list, Graphics g,
			int index, int y, int w) {
		SearchResult s = (SearchResult) rows.elementAt(index);
		//SearchList sl = (SearchList) list;
		
		Bitmap b = EncodedImage.getEncodedImageResource(Utils.DEFAULT_BIZ_PIC).getBitmap();
		
		g.drawBitmap(10, y+10, b.getWidth(), b.getHeight(), b, 0, 0);
		g.setFont(g.getFont().derive(Font.BOLD, 6, Ui.UNITS_pt));
		g.drawText(s.getBusinessName(), b.getWidth()+20, y, Font.BOLD, w);
		
		// Rating
		String rateImg = "s_rate" + s.getBusinessRating() + ".png";
		Bitmap c = EncodedImage.getEncodedImageResource(rateImg).getBitmap();
		g.drawBitmap(20+b.getWidth(), y+20, c.getWidth(), c.getHeight(), c, 0, 0);
		
		// Address
		boolean gotParent = (s.getBusinessParentName().compareTo("") == 0) ? false : true;
		String addrField = (!gotParent) ? s.getBusinessAddress1() : s.getBusinessParentName();
		if(gotParent) {
			g.setColor(Color.GREEN);
			g.setFont(g.getFont().derive(Font.PLAIN|Font.UNDERLINED, 5, Ui.UNITS_pt));
		} else {
			g.setFont(g.getFont().derive(Font.PLAIN, 5, Ui.UNITS_pt));
		}
		g.drawText(addrField, b.getWidth()+20, y+25+c.getHeight(), Font.PLAIN, w);
		
		// Review
		String reviewField = s.getBusinessReview().toString();
		int endIndex = (reviewField.length() > 50) ? 50 : reviewField.length();
		reviewField = "\"" + reviewField.substring(0, endIndex) + "...\"";
		g.setFont(g.getFont().derive(Font.ITALIC, 5, Ui.UNITS_pt));
		g.setColor(Color.BLACK);
		g.drawText(reviewField, b.getWidth()+20, y+25+c.getHeight()+20, Font.PLAIN, w);
		
		int rowHeight = b.getHeight() + 20;
		if(s.getBusinessReview().compareTo("") != 0) {
			rowHeight += 20;
		}
		
		//list.setRowHeight(rowHeight);
		list.setRowHeight(index, rowHeight);
		
		b = c = null;
	}
	
	protected boolean navigationClick(int status, int time) {
		SearchResult s = (SearchResult) rows.elementAt(getSelectedIndex());
		String url = s.getBusinessMobileWebURL()+Utils.MOBILE_WEB_UTM;
		
		BrowserSession site = Browser.getDefaultSession();
        site.displayPage(url);
        site.showBrowser();
		
		return true;
	}

	public Object get(ListField listField, int index) {
		return null;
	}

	public int getPreferredWidth(ListField listField) {
		return Display.getWidth();
	}

	public int indexOfList(ListField listField, String prefix, int start) {
		return listField.indexOfList(prefix, start);
	}
	
	public void addRow(SearchResult s) {
		rows.addElement(s);
	}
	
}
