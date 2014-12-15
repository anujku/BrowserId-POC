package com.codelaboration.poc.browserid.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class VerifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SERVER_HOST = "127.0.0.1";
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletOutputStream out = resp.getOutputStream();
		
		String assertion = (String) req.getParameter("assertion");
		
		// Request verification to mozilla browserID service
		String verifyResponse = getVerifyResponse(assertion, SERVER_HOST);
		if(verifyResponse==null) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		// Parse the JSON response
		JSONObject json = (JSONObject) JSONSerializer.toJSON(verifyResponse);
		String status = json.getString("status");
		String email = json.getString("email");
		
		if("okay".equals(status)) {
			// Set the session variable user to email
			HttpSession session = req.getSession();
			if(session==null) {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} else {
				session.setAttribute(SecurityFilter.USER_ID_ATTRIBUTE, email);
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);			
		}
		
		out.println("Welcome " + email);
	}
	
	private String getVerifyResponse(String assertion, String audience) throws IOException {
		String retVal = null;
		try {
			URL url = new URL("https://browserid.org/verify");
			String urlParameters =
					"assertion=" + assertion + 
					"&audience=" + audience;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			
			conn.setUseCaches (false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream (conn.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();
			
			
			if (conn.getResponseCode() != 200) {
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			
			String output;
			while ((output = br.readLine()) != null) {
				retVal = output;
			}

			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			retVal = null;
		}
		return retVal;
	}
}
