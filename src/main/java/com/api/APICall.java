package com.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class APICall {
	private static boolean hasSessionId = false;
	private static String sessionId;

	private static String getSessionId(HttpURLConnection connection) {
		final String cookie = connection.getHeaderFields().get("Set-Cookie").get(0);
		final int indexOf = cookie.indexOf("JSESSIONID=");
		return cookie.substring(indexOf + "JSESSIONID=".length(), cookie.indexOf(';', indexOf));
	}
	
	public static String getSessionId() {
		return sessionId;
	}

	public static void setSessionId(String sessionId) {
		APICall.sessionId = sessionId;
		APICall.hasSessionId = true;
	}

	public static String call(String request) {
		URL url;
		StringBuffer content = new StringBuffer();

        try {
        	url = new URL("http://35.180.135.191:8080/" + request);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (hasSessionId) {
            	con.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            if (!hasSessionId) {
            	sessionId = getSessionId(con);
            	hasSessionId = true;
            }

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
        } catch (MalformedURLException e)  {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return content.toString();
	}
}
