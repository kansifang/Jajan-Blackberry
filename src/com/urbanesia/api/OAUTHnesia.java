package com.urbanesia.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.crypto.MD5Digest;

import com.urbanesia.utils.Utils;

public class OAUTHnesia {
	protected static final String BASE_URL = "http://api1.urbanesia.com/";
	public static final int OAUTH_SAFE_ENCODE = 1;
	public static final int OAUTH_NO_SAFE_ENCODE = 0;
	
	private static String CONSUMER_KEY;
	private static String CONSUMER_SECRET;
	private static String USER_KEY;
	private static String USER_SECRET;
	
	private static String API_URI;
	private static String SAFE_ENCODE = "0";
	
	public OAUTHnesia(String cons_key, String cons_secret, int safe_encode) {
		setConsumerKey(cons_key);
		setConsumerSecret(cons_secret);
		if(safe_encode == 1)
			setSafeEncode("1");
	}
	
	public OAUTHnesia(String cons_key, String cons_secret, String user_key, String user_secret, int safe_encode) {
		setConsumerKey(cons_key);
		setConsumerSecret(cons_secret);
		setUserKey(user_key);
		setUserSecret(user_secret);
		if(safe_encode == 1)
			setSafeEncode("1");
	}
	
	public String oAuth(String oUri, String post, String get) {
		setApiUri(oUri);
		
		String oPost = "oauth_consumer_key=" + getConsumerKey()
			+ "&oauth_nonce=" + getNonce() + "&oauth_signature_method="
			+ "HMAC-SHA1" + "&oauth_timestamp=" + getTime()
			+ "&oauth_token=" + getUserKey() + "&oauth_version=" + "1.0";
		if (post.compareTo("") == 0) {
			post = oPost;
		} else {
			post += "&" + oPost;
		}
		
		if (getSafeEncode().compareTo("1") == 0) {
			post += "&safe_encode=" + Integer.toString(OAUTH_SAFE_ENCODE);
		}
		
		if (get.compareTo("") != 0) {
			get = "&" + get;
		}
		
		String request = post + get;
		String requestify = encodeForOAuth(request);
		
		String base_sig = generateBaseSignature(requestify);
		//Log.out(base_sig);
		
		try {
			String signature = Utils.hmacsha1(getConsumerSecret() + "&"
					+ getUserSecret(), base_sig);
			String oauth_sig = "?oauth_signature=";
			oauth_sig += Utils.URLencode(signature);
			String url = BASE_URL + getApiUri() + oauth_sig + get;
			return sendRequest(url, post);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String sendRequest(String url, String post) {
		String urlString = url + ConnString.getConnectionString();
		//Log.out("Querying to Urbanesia..");
		
		try {
			HttpConnection conn = (HttpConnection) Connector.open(urlString);
			OutputStream os = null;
			InputStream in = null;
			
			try {
				conn.setRequestMethod(HttpConnection.POST);
				conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
				conn.setRequestProperty("If-Modified-Since","29 Oct 1999 19:43:31 GMT");
				conn.setRequestProperty("User-Agent","Urbanesia Jajan Blackberry v1.0");
				conn.setRequestProperty("Content-Language", "en-US");
				
				os = conn.openOutputStream();
				StringBuffer buffer = new StringBuffer();
				buffer.append(post);
				os.write(buffer.toString().getBytes());
				os.flush();
				
				in = conn.openInputStream();
				byte[] data = new byte[20];
				int len = 0;
				int size = 0;
				StringBuffer raw = new StringBuffer();
				while ( -1 != (len = in.read(data)) ) {
                    String received = new String(data, 0, len);
                    raw.append(received);
                    size += len;
                }
				in.close();
				
				String msg = raw.toString();
				
				return msg;
			} catch(Exception e) {
				//e.printStackTrace();
				return "";
			} finally {
				if(conn != null) {
					try {
						conn.close();
					} catch(Exception e2) {
						//e2.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			//e.printStackTrace();
			return "";
		}
		
	}
	
	private String generateBaseSignature(String s) {
		return "POST&" + Utils.URLencode(BASE_URL + getApiUri()) + "&"
			+ Utils.URLencode(s);
	}
	
	private String encodeForOAuth(String s) {
		String[] par = Utils.split(s, "&");
		par = Utils.sort(par);
		//Log.out(par[0]);
		
		int max = par.length;
		int j = 0;
		String postify = "";
		for (int i = 0; i < max; i++) {
			if (j == 1)
				postify += "&";
			String[] temp = Utils.split(par[i], "=");
				postify += Utils.URLencode(temp[0]) + "="
						+ Utils.URLencode(temp[1]);
			j = 1;
		}
		
		return postify;
	}
	
	private String getNonce() {
		return md5(Long.toString(System.currentTimeMillis()));
	}
	
	private String getTime() {
		return Long.toString(System.currentTimeMillis());
	}
	
	private String md5(String s) {
		MD5Digest digest = new MD5Digest();
		digest.update(s.getBytes());
		return new String(s.getBytes());
	}
	
	public void setApiUri(String s) {
		OAUTHnesia.API_URI = s;
	}

	public String getApiUri() {
		return OAUTHnesia.API_URI;
	}

	public void setSafeEncode(String s) {
		OAUTHnesia.SAFE_ENCODE = s;
	}

	public String getSafeEncode() {
		return OAUTHnesia.SAFE_ENCODE;
	}

	public void setConsumerKey(String s) {
		OAUTHnesia.CONSUMER_KEY = s;
	}

	public String getConsumerKey() {
		return OAUTHnesia.CONSUMER_KEY;
	}

	public void setConsumerSecret(String s) {
		OAUTHnesia.CONSUMER_SECRET = s;
	}

	public String getConsumerSecret() {
		return OAUTHnesia.CONSUMER_SECRET;
	}

	public void setUserKey(String s) {
		OAUTHnesia.USER_KEY = s;
	}

	public String getUserKey() {
		return OAUTHnesia.USER_KEY;
	}

	public void setUserSecret(String s) {
		OAUTHnesia.USER_SECRET = s;
	}

	public String getUserSecret() {
		return OAUTHnesia.USER_SECRET;
	}
}
