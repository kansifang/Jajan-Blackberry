package com.urbanesia.utils;

import java.io.IOException;

import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;

public class Utils {
	public static final String APP_NAME = "Jajan";
	public static final String JAJAN_UUID = "be06d170-c5e7-11e0-9572-0800200c9a66";
	
	public static final String SEARCH_SUBCAT = "Jajanan,Culinary";
	public static final String DEFAULT_BIZ_PIC = "default_biz.png";
	public static final String TOP_BAR_BG = "bb_top_bar.png";
	public static final String ICON_SMALL = "jajan_small.png";
	public static final String MOBILE_WEB_URL = "http://m.urbanesia.com/";
	public static final String MOBILE_WEB_BIZ_PROFILE = MOBILE_WEB_URL + "profile/";
	public static final String MOBILE_WEB_UTM = "?from=jajan_bb";
	
	public static final String CONSUMER_KEY = "CONSUMER_KEY";
	public static final String CONSUMER_SECRET = "CONSUMER_SECRET";
	public static final String USER_KEY = "USER_KEY";
	public static final String USER_SECRET = "USER_SECRET";
	
	public static final String COORDS_DEFAULT_JKT = "-6.17535,106.827257";
	public static final String COORDS_DEFAULT_BALI = "-8.657054,115.217461";
	public static final String COORDS_DEFAULT_BANDUNG = "-6.921799,107.607077";
	
	public static final String BBM_CONNECTED_YET = "Have you connected Jajan with your BBM profile?";
	
	public static AnimatedGIFField getLoadingGif() {
		GIFEncodedImage f = (GIFEncodedImage) GIFEncodedImage.getEncodedImageResource("loading.gif");
		return new AnimatedGIFField(f);
	}
	
	public static String hmacsha1(String key, String message) throws CryptoTokenException, CryptoUnsupportedOperationException, IOException {
		HMACKey k = new HMACKey(key.getBytes());
		HMAC hmac = new HMAC(k, new SHA1Digest());
		hmac.update(message.getBytes());
		byte[] mac = hmac.getMAC();
		return Base64OutputStream.encodeAsString(mac, 0, mac.length, false, false);
	}
	
	public static String URLencode(String s) {
	    if (s!=null) {
	        StringBuffer tmp = new StringBuffer();
	        int i=0;
	        try {
	            while (true) {
	            	char t = s.charAt(i);
	                int b = (int) t;
	                if (
	                		t == (char) '.' || 
	                		t == (char) '_' || 
	                		t == (char) '-' ||
	                		t == (char) '~' ||
	                		(b>=0x30 && b<=0x39) || 
	                		(b>=0x41 && b<=0x5A) || 
	                		(b>=0x61 && b<=0x7A)
	                	) {
	                    tmp.append((char)b);
	                }
	                else {
	                    tmp.append("%");
	                    if (b <= 0xf) tmp.append("0");
	                    tmp.append(Integer.toHexString(b).toUpperCase());
	                }
	                i++;
	            }
	        }
	        catch (Exception e) {}
	        return tmp.toString();
	    }
	    return null;
	}
	
	private static class SortComparator implements Comparator {

		public int compare(Object key1, Object key2) {
			return ((((String) key1).compareTo((String) key2)));
		}
		
	}
	
	public static String[] sort(String[] e) {
		SimpleSortingVector v = new SimpleSortingVector();
        for(int i=0, max=e.length; i<max; i++) {
        	v.addElement(e[i]);
        	//Log.out("Unsorted Params " + String.valueOf(i) + ": " + e[i]);
        }
        
        v.setSortComparator(new SortComparator());
        v.reSort();
        
        int max = v.size();
        String[] ret = new String[max];
        for(int i=0; i<max; i++) {
        	ret[i] = (String) v.elementAt(i);
        	//Log.out("Sorted Params " + String.valueOf(i) + ": " + ret[i]);
        }
        
        return ret;
    }
	
	public static String[] split(String strString, String strDelimiter) {
	    String[] strArray;
	    int iOccurrences = 0;
	    int iIndexOfInnerString = 0;
	    int iIndexOfDelimiter = 0;
	    int iCounter = 0;

	    if (strString == null) {
	        throw new IllegalArgumentException("Input string cannot be null.");
	    }

	    if (strDelimiter.length() <= 0 || strDelimiter == null) {
	        throw new IllegalArgumentException("Delimeter cannot be null or empty.");
	    }

	    if (strString.startsWith(strDelimiter)) {
	        strString = strString.substring(strDelimiter.length());
	    }

	    if (!strString.endsWith(strDelimiter)) {
	        strString += strDelimiter;
	    }

	    while((iIndexOfDelimiter = strString.indexOf(strDelimiter,
	           iIndexOfInnerString)) != -1) {
	        iOccurrences += 1;
	        iIndexOfInnerString = iIndexOfDelimiter +
	            strDelimiter.length();
	    }

	    strArray = new String[iOccurrences];

	    iIndexOfInnerString = 0;
	    iIndexOfDelimiter = 0;

	    while((iIndexOfDelimiter = strString.indexOf(strDelimiter,
	           iIndexOfInnerString)) != -1) {

	        strArray[iCounter] = strString.substring(iIndexOfInnerString,iIndexOfDelimiter);

	        iIndexOfInnerString = iIndexOfDelimiter +
	            strDelimiter.length();

	        iCounter += 1;
	    }

	    return strArray;
	}
	
}
