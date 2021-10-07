package com.ilhsk.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHttp {
	private static final Logger logger = LoggerFactory.getLogger(GetHttp.class);
	final static double VERSION = 1.20141016;
	static final int ERROR_CODE_HTTP_NOT_FOUND = -404;
	static final int ERROR_CODE_HTTP_UNAUTHORIZED = -401;
	static final int ERROR_CODE_HTTP_ELSE = -1;
	static final int ERROR_CODE_HTTP_EXCEPTION = -1000;
	static final int ERROR_CODE_NOERROR = 0;
	byte[] htmlByte = null;
	public int errorCode = 0;
	boolean bUseCache = true;
	boolean m_session = false;
	String m_cookies = "";
	String url = "";

	public String getString() {
		try {
			return new String(htmlByte, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] getByte() {
		return htmlByte;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public boolean get(String addr) {
		
		return get(addr, null);
	}
	public boolean get(String addr, String cookies) {
		
		return execute(addr,null,"GET","application/x-www-form-urlencoded;charset=utf-8", 15, 30,cookies);
	}
	public boolean post(String addr, String parameters) {
		return post(addr, parameters, null);
	}
	public boolean post(String addr, String parameters, String cookies) {
		
		return execute(addr,parameters,"POST","application/json", 5, 10,cookies);
	}
	

	public String getM_cookies() {
		return m_cookies;
	}

	public void setM_cookies(String m_cookies) {
		this.m_cookies = m_cookies;
	}

	public void saveCookie(HttpURLConnection conn) {
		this.m_cookies ="";
		Map<String, List<String>> imap = conn.getHeaderFields();
		if (imap.containsKey("Set-Cookie")) {
			List<String> lString = imap.get("Set-Cookie");
			for (int i = 0; i < lString.size(); i++) {				
				m_cookies += lString.get(i);
				//System.out.println(m_cookies);
			}

			m_session = true;
		} else {
			m_session = false;
		}
		//logger.debug("Cookie = > {}", m_cookies);
	}

	// 참고 소스:http://markan82.tistory.com/32
	public boolean execute(String addr,String parameters, String method, String contentType, int connTimeOutSec, int readTimeOutSec,String cookies) {
		this.url = addr;
		boolean retval = true;		
		errorCode = ERROR_CODE_NOERROR;
		try {
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(1000);
			if (conn != null) {
				conn.setRequestProperty("Content-Type", contentType);
				if(cookies!=null) {
					conn.setRequestProperty("Cookie", cookies);
					logger.debug("Cookie:{}", cookies);
				}else if (m_session) {
					conn.setRequestProperty("Cookie", m_cookies);
					logger.debug("m_session:{}", m_cookies);
				}
				conn.setRequestMethod(method);
				conn.setConnectTimeout(1000 * connTimeOutSec);
				conn.setReadTimeout(1000 * readTimeOutSec);
				conn.setUseCaches(bUseCache);
				conn.setDoInput(true);
				if(!StringUtils.isEmpty(parameters)) {
					conn.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(conn.getOutputStream()); 
					wr.writeBytes(parameters); 
					wr.flush(); 
					wr.close();
				}
				int resCode = conn.getResponseCode();
				saveCookie(conn);

				if (resCode == HttpURLConnection.HTTP_OK) {
					htmlByte = inputStreamToByte(conn.getInputStream());
					if (htmlByte == null || htmlByte.length == 0) {
						errorCode = ERROR_CODE_HTTP_ELSE;
						retval = false;
					}
				} else if (resCode == HttpURLConnection.HTTP_NOT_FOUND) {
					errorCode = ERROR_CODE_HTTP_NOT_FOUND;
					retval = false;
				} else if (resCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
					errorCode = ERROR_CODE_HTTP_UNAUTHORIZED;
					retval = false;
				} else {
					errorCode = ERROR_CODE_HTTP_ELSE;
					retval = false;
				}
				// DISCONNECT
				conn.disconnect();
			} else {
				errorCode = ERROR_CODE_HTTP_ELSE;
				retval = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorCode = ERROR_CODE_HTTP_EXCEPTION;
			retval = false;
		}
		return retval;
	}
	
	

	private byte[] inputStreamToByte(InputStream in) {
		final int BUF_SIZE = 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUF_SIZE];
		try {
			int length;
			while ((length = in.read(buffer)) != -1)
				out.write(buffer, 0, length);
		} catch (IOException e) {
			logger.error("url : {} "+e.getMessage(),this.url,e);
			return null;
		}
		return out.toByteArray();
	}
}