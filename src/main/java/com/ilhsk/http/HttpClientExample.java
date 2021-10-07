package com.ilhsk.http;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import net.sf.json.JSONObject;

public class HttpClientExample {

	// one instance, reuse
	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	private CookieStore cookieStore;

	private String cookieString;

	public static void main(String[] args) throws Exception {
			
		GetHttp http = new GetHttp();

		JSONObject json = new JSONObject();
		json.put("id", "test");
		json.put("password", "test");
	
		if (http.post("http://127.0.0.1:8080/login", json.toString())) {
			System.out.println(">> "+http.getString());
		} else {
			System.out.println("code" + http.errorCode);
		}
	
		if (http.get("http://127.0.0.1:8080/sample")) {
			System.out.println(">> "+http.getString());
		} else {
			System.out.println("code" + http.errorCode);
		}
		
	
			
	
		
	}


}
