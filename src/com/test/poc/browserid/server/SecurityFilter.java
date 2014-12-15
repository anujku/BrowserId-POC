package com.test.poc.browserid.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SecurityFilter implements Filter {

	public static final String USER_ID_ATTRIBUTE = "userId";

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest  httprequest = (HttpServletRequest)  request;
		HttpServletResponse  httpresponse = (HttpServletResponse) response;
		try {
			HttpSession session = httprequest.getSession(true);
			String requestUri = httprequest.getRequestURI().toLowerCase();

			if (session.getAttribute(USER_ID_ATTRIBUTE) !=  null) {
				chain.doFilter(request, response);
				return;
			} else {
				// Unless is an unprotected resource, redirect to login
				if (	"/login.jsp".equals(requestUri) ||
						"/api/verify".equals(requestUri) ||
						requestUri.endsWith("css") ||
						requestUri.endsWith("jpg") ||
						requestUri.endsWith("gif") ||
						requestUri.endsWith("png")) {
					chain.doFilter(request, response);
					return;
				} else {
					httpresponse.sendRedirect("/login.jsp");
				}
			}
		} catch (Exception w) {
			w.printStackTrace();
		}
	}

	@Override
	public void init(FilterConfig dummy) throws ServletException {
	}
}